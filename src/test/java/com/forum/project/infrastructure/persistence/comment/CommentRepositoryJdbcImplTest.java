package com.forum.project.infrastructure.persistence.comment;

import com.forum.project.domain.comment.Comment;
import com.forum.project.domain.comment.CommentKey;
import com.forum.project.domain.comment.CommentRepository;
import com.forum.project.domain.comment.CommentStatus;
import com.forum.project.infrastructure.persistence.JdbcTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
@ActiveProfiles("test")
@Slf4j
class CommentRepositoryJdbcImplTest {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        commentRepository = new CommentRepositoryJdbcImpl(jdbcTemplate);
        JdbcTestUtils.dropTable(jdbcTemplate, "comments");
        JdbcTestUtils.createTable(jdbcTemplate, "comments",
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "question_id BIGINT NOT NULL," +
                "parent_comment_id BIGINT," +
                "content VARCHAR(1000) NOT NULL, " +
                "up_voted_count BIGINT DEFAULT 0 CHECK (up_voted_count >= 0), " +
                "down_voted_count BIGINT DEFAULT 0 CHECK (down_voted_count >= 0)," +
                "status VARCHAR(50)," +
                "report_count BIGINT DEFAULT 0 CHECK (report_count >= 0)," +
                "is_edited BOOLEAN DEFAULT FALSE," +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (parent_comment_id) REFERENCES comments(id), " +
                "CHECK (status IN ('ACTIVE', 'INACTIVE', 'DELETED', 'SPAM'))");
    }

    private Comment createComment(Long userId, Long questionId, Long parentCommentId) {
        return Comment.builder()
                .userId(userId)
                .questionId(questionId)
                .parentCommentId(parentCommentId)
                .content("testContent")
                .build();
    }

    private void insertTestData(Long userId, Long questionId, Long parentCommentId, String status) {
        String sql = CommentQueries.insert();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("questionId", questionId)
                .addValue("parentCommentId", parentCommentId)
                .addValue("content", "testContent")
                .addValue("status", status);
        jdbcTemplate.update(sql, params);
    }

    private Optional<Comment> findData(Long id) {
        String sql = CommentQueries.findById();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            Comment response = jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Comment.class));
            return Optional.ofNullable(response);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Test
    void insertAndReturnGeneratedKeys() {
        Map<String, Object> generatedKeys = commentRepository.insertAndReturnGeneratedKeys(
                createComment(1L, 1L, null));

        assertThat(generatedKeys).hasSize(CommentKey.getKeys().length);

        assertThat((Long) generatedKeys.get(CommentKey.ID)).isEqualTo(1L);
        assertThat(((Timestamp) generatedKeys.get(CommentKey.CREATED_DATE)).toInstant())
                .isBeforeOrEqualTo(Timestamp.valueOf(LocalDateTime.now()).toInstant());
        assertThat(((Timestamp) generatedKeys.get(CommentKey.LAST_MODIFIED_DATE)).toInstant())
                .isBeforeOrEqualTo(Timestamp.valueOf(LocalDateTime.now()).toInstant());
    }

    @Test
    void findById() {
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());

        Optional<Comment> result = commentRepository.findById(1L);

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(comment -> {
                    assertThat(comment.getId()).isOne();
                    assertThat(comment.getQuestionId()).isOne();
                    assertThat(comment.getParentCommentId()).isNull();
                    assertThat(comment.getContent()).isEqualTo("testContent");
                    assertThat(comment.getStatus()).isEqualTo(CommentStatus.ACTIVE.name());
                });

        result.ifPresent(comment -> log.info(comment.toString()));
    }

    @Test
    void findAllByQuestionId() {
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());
        insertTestData(2L, 1L, null, CommentStatus.ACTIVE.name());
        insertTestData(3L, 2L, null, CommentStatus.ACTIVE.name());

        List<Comment> result = commentRepository.findAllByQuestionId(1L);

        assertThat(result)
                .hasSize(2)
                .extracting(Comment::getQuestionId)
                .containsOnly(1L);
    }

    @Test
    void findAllByParentCommentId() {
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());
        insertTestData(2L, 1L, 1L, CommentStatus.ACTIVE.name());
        insertTestData(3L, 1L, 1L, CommentStatus.ACTIVE.name());
        insertTestData(1L, 2L, null, CommentStatus.ACTIVE.name());

        List<Comment> result = commentRepository.findAllByParentCommentId(1L);

        assertThat(result)
                .hasSize(2)
                .extracting(Comment::getParentCommentId)
                .containsOnly(1L);
    }

    @Test
    void findAllByUserId() {
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());
        insertTestData(1L, 2L, null, CommentStatus.ACTIVE.name());
        insertTestData(2L, 1L, null, CommentStatus.ACTIVE.name());
        insertTestData(3L, 2L, null, CommentStatus.ACTIVE.name());

        List<Comment> result = commentRepository.findAllByUserId(1L);

        assertThat(result)
                .hasSize(2)
                .extracting(Comment::getUserId)
                .containsOnly(1L);
    }

    @Test
    void updateContent() {
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());

        int result = commentRepository.updateContent(1L, "newContent");

        assertThat(result).isOne();

        assertThat(findData(1L))
                .isPresent()
                .hasValueSatisfying(comment -> {
                    assertThat(comment.getContent()).isEqualTo("newContent");
                    assertThat(comment.getQuestionId()).isEqualTo(1L);
                });
    }

    @Test
    void updateUpVotedCount() {
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());

        int result = commentRepository.updateUpVotedCount(1L, 2L);

        assertThat(result).isOne();

        assertThat(findData(1L))
                .isPresent()
                .hasValueSatisfying(comment -> {
                    assertThat(comment.getUpVotedCount()).isEqualTo(2L);
                    assertThat(comment.getQuestionId()).isEqualTo(1L);
                });
    }

    @Test
    void updateDownVotedCount() {
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());

        int result = commentRepository.updateDownVotedCount(1L, 2L);

        assertThat(result).isOne();
        assertThat(findData(1L))
                .isPresent()
                .hasValueSatisfying(comment -> {
                    assertThat(comment.getDownVotedCount()).isEqualTo(2L);
                    assertThat(comment.getQuestionId()).isEqualTo(1L);
                });
    }

    @Test
    void deleteById() {
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());
        assertThat(findData(1L)).isPresent();

        commentRepository.deleteById(1L);
        assertThat(findData(1L)).isEmpty();

        assertThatCode(() -> commentRepository.deleteById(999L)).doesNotThrowAnyException();
    }

    @Test
    void existsById() {
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());

        boolean result = commentRepository.existsById(1L);

        assertThat(result).isTrue();
    }

    @Test
    void existsById_notExists() {
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());

        boolean result = commentRepository.existsById(2L);

        assertThat(result).isFalse();
    }
}