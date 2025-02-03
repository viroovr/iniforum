package com.forum.project.infrastructure.persistence.comment;

import com.forum.project.common.utils.DateUtils;
import com.forum.project.domain.comment.Comment;
import com.forum.project.domain.comment.CommentKey;
import com.forum.project.domain.comment.CommentRepository;
import com.forum.project.domain.comment.CommentStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
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
import static org.assertj.core.api.Assertions.tuple;

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

        jdbcTemplate.getJdbcTemplate().execute("CREATE TABLE comments (" +
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
                "CHECK (status IN ('ACTIVE', 'INACTIVE', 'DELETED', 'SPAM'))" +
                ");");
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.getJdbcTemplate().execute("DROP TABLE IF EXISTS comments;");
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
        Comment comment = createComment(1L, 1L, null);
        Timestamp expectedTimestamp = Timestamp.valueOf(LocalDateTime.now());

        Map<String, Object> generatedKeys = commentRepository.insertAndReturnGeneratedKeys(comment);

        assertThat(generatedKeys).isNotNull();
        assertThat(generatedKeys.size()).isEqualTo(CommentKey.getKeys().length);

        Long generatedId = (Long) generatedKeys.get(CommentKey.ID);
        Timestamp createdDate = (Timestamp) generatedKeys.get(CommentKey.CREATED_DATE);
        Timestamp lastModifiedDate = (Timestamp) generatedKeys.get(CommentKey.LAST_MODIFIED_DATE);

        assertThat(generatedId).isEqualTo(1L);
        assertThat(createdDate).isNotNull();
        assertThat(lastModifiedDate).isNotNull();
        assertThat(DateUtils.timeDifferenceWithinLimit(expectedTimestamp, createdDate)).isTrue();
        assertThat(DateUtils.timeDifferenceWithinLimit(expectedTimestamp, lastModifiedDate)).isTrue();
    }

    @Test
    void findById() {
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());

        Optional<Comment> optionalComment = commentRepository.findById(1L);

        assertThat(optionalComment)
                .isPresent()
                .hasValueSatisfying(comment -> {
                    assertThat(comment.getId()).isOne();
                    assertThat(comment.getQuestionId()).isOne();
                    assertThat(comment.getParentCommentId()).isNull();
                    assertThat(comment.getLoginId()).isNull();
                    assertThat(comment.getContent()).isEqualTo("testContent");
                    assertThat(comment.getUpVotedCount()).isZero();
                    assertThat(comment.getDownVotedCount()).isZero();
                    assertThat(comment.getStatus()).isEqualTo(CommentStatus.ACTIVE.name());
                    assertThat(comment.getReportCount()).isZero();
                    assertThat(comment.getIsEdited()).isFalse();
                });

        log.info(optionalComment.get().toString());
    }

    @Test
    void findAllByQuestionId() {
        Long questionId = 1L;
        insertTestData(1L, questionId, null, CommentStatus.ACTIVE.name());
        insertTestData(2L, questionId, null, CommentStatus.ACTIVE.name());
        insertTestData(3L, 2L, null, CommentStatus.ACTIVE.name());

        List<Comment> result = commentRepository.findAllByQuestionId(questionId);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result)
            .extracting("userId", "questionId", "parentCommentId", "status", "content")
            .containsExactlyInAnyOrder(
                    tuple(1L, questionId, null, CommentStatus.ACTIVE.name(), "testContent"),
                    tuple(2L, questionId, null, CommentStatus.ACTIVE.name(), "testContent")
            );
    }

    @Test
    void findAllByParentCommentId() {
        Long parentCommentId = 1L;
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());
        insertTestData(2L, 1L, parentCommentId, CommentStatus.ACTIVE.name());
        insertTestData(3L, 1L, parentCommentId, CommentStatus.ACTIVE.name());
        insertTestData(1L, 2L, null, CommentStatus.ACTIVE.name());

        List<Comment> result = commentRepository.findAllByParentCommentId(parentCommentId);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result)
            .extracting("userId", "questionId", "parentCommentId", "status", "content")
            .containsExactlyInAnyOrder(
                tuple(2L, 1L, parentCommentId, CommentStatus.ACTIVE.name(), "testContent"),
                tuple(3L, 1L, parentCommentId, CommentStatus.ACTIVE.name(), "testContent")
            );
    }

    @Test
    void findAllByUserId() {
        Long userId = 1L;
        insertTestData(userId, 1L, null, CommentStatus.ACTIVE.name());
        insertTestData(userId, 2L, null, CommentStatus.ACTIVE.name());
        insertTestData(2L, 1L, null, CommentStatus.ACTIVE.name());
        insertTestData(3L, 2L, null, CommentStatus.ACTIVE.name());

        List<Comment> result = commentRepository.findAllByUserId(userId);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result)
                .extracting("userId", "questionId", "parentCommentId", "status", "content")
                .containsExactlyInAnyOrder(
                        tuple(userId, 1L, null, CommentStatus.ACTIVE.name(), "testContent"),
                        tuple(userId, 2L, null, CommentStatus.ACTIVE.name(), "testContent")
                );
    }

    @Test
    void updateContent() {
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());
        commentRepository.updateContent(1L, "newContent");

        Optional<Comment> optionalComment = findData(1L);
        assertThat(optionalComment).isPresent();

        Comment comment = optionalComment.get();
        assertThat(comment.getContent()).isEqualTo("newContent");
        assertThat(comment.getQuestionId()).isEqualTo(1L);
        log.info(comment.toString());
    }

    @Test
    void updateUpVotedCount() {
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());

        commentRepository.updateUpVotedCount(1L, 2L);

        Optional<Comment> optionalComment = findData(1L);
        assertThat(optionalComment).isPresent();

        Comment comment = optionalComment.get();
        assertThat(comment.getUpVotedCount()).isEqualTo(2L);
        assertThat(comment.getQuestionId()).isEqualTo(1L);
        log.info(comment.toString());
    }

    @Test
    void updateDownVotedCount() {
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());

        commentRepository.updateDownVotedCount(1L, 2L);

        Optional<Comment> optionalComment = findData(1L);
        assertThat(optionalComment).isPresent();

        Comment comment = optionalComment.get();
        assertThat(comment.getDownVotedCount()).isEqualTo(2L);
        assertThat(comment.getQuestionId()).isEqualTo(1L);
        log.info(comment.toString());
    }

    @Test
    void deleteById() {
        insertTestData(1L, 1L, null, CommentStatus.ACTIVE.name());

        commentRepository.deleteById(1L);
        Optional<Comment> optionalComment = findData(1L);

        assertThat(optionalComment).isEmpty();
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