package com.forum.project.infrastructure.persistence.comment.like;

import com.forum.project.domain.like.LikeStatus;
import com.forum.project.domain.like.commentlike.CommentLike;
import com.forum.project.domain.like.commentlike.CommentLikeKey;
import com.forum.project.domain.like.commentlike.CommentLikeRepository;
import com.forum.project.infrastructure.persistence.JdbcTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
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

@Slf4j
@JdbcTest
@ActiveProfiles("test")
class CommentLikeRepositoryJdbcImplTest {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private CommentLikeRepository commentLikeRepository;

    @BeforeEach
    void setUp() {
        commentLikeRepository = new CommentLikeRepositoryJdbcImpl(jdbcTemplate);
        JdbcTestUtils.dropTable(jdbcTemplate, "comment_likes");
        JdbcTestUtils.createTable(jdbcTemplate, "comment_likes",
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "comment_id BIGINT NOT NULL, " +
                "status ENUM('LIKE', 'DISLIKE', 'NONE') DEFAULT 'LIKE', " +
                "ip_address VARCHAR(255), " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ");
    }

    private CommentLike createCommentLike(Long userId, Long commentId, String status) {
        return CommentLike.builder()
                .userId(userId)
                .commentId(commentId)
                .status(status)
                .build();
    }

    private void insertTestData(Long userId, Long commentId, String status) {
        String sql = CommentLikeQueries.insertAndReturnGeneratedKeys();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("commentId", commentId)
                .addValue("status", status)
                .addValue("ipAddress", "192.168.0.1");

        jdbcTemplate.update(sql, params);
    }

    private Optional<CommentLike> findData(Long id) {
        return JdbcTestUtils.findData(jdbcTemplate, CommentLikeQueries.findById(), id, CommentLike.class);
    }

    @Test
    void insertAndReturnGeneratedKeys() {
        Map<String, Object> result = commentLikeRepository.insertAndReturnGeneratedKeys(
                createCommentLike(1L, 1L, LikeStatus.LIKE.name()));

        assertThat(result).hasSize(CommentLikeKey.getKeys().length);

        assertThat((Long) result.get(CommentLikeKey.ID)).isEqualTo(1L);
        assertThat(((Timestamp) result.get(CommentLikeKey.CREATED_DATE)).toInstant())
                .isBeforeOrEqualTo(Timestamp.valueOf(LocalDateTime.now()).toInstant());
    }

    @Test
    void existsByUserIdAndCommentId_fail() {
        boolean result = commentLikeRepository.existsByUserIdAndCommentId(1L, 1L);

        assertThat(result).isFalse();
    }

    @Test
    void existsByUserIdAndCommentId() {
        insertTestData(1L, 1L, LikeStatus.LIKE.name());

        boolean result = commentLikeRepository.existsByUserIdAndCommentId(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    void findByUserIdAndCommentId() {
        insertTestData(1L, 1L, LikeStatus.LIKE.name());
        Optional<CommentLike> result = commentLikeRepository.findByUserIdAndCommentId(1L, 1L);

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(commentLike -> {
                    assertThat(commentLike.getUserId()).isOne();
                    assertThat(commentLike.getCommentId()).isOne();
                });
    }

    @Test
    void delete() {
        insertTestData(1L, 1L, LikeStatus.LIKE.name());
        assertThat(findData(1L)).isPresent();

        commentLikeRepository.delete(1L);
        assertThat(findData(1L)).isEmpty();

        assertThatCode(() -> commentLikeRepository.delete(999L)).doesNotThrowAnyException();
    }

    @Test
    void findCommentIdsByUserIdAndStatus() {
        insertTestData(1L, 1L, LikeStatus.LIKE.name());
        insertTestData(1L, 2L, LikeStatus.LIKE.name());
        insertTestData(2L, 1L, LikeStatus.LIKE.name());
        insertTestData(1L, 3L, LikeStatus.DISLIKE.name());

        List<Long> result = commentLikeRepository.findCommentIdsByUserIdAndStatus(1L, LikeStatus.LIKE.name());

        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void updateStatus() {
        insertTestData(1L, 1L, LikeStatus.LIKE.name());

        int result = commentLikeRepository.updateStatus(1L, LikeStatus.DISLIKE.name());
        assertThat(result).isEqualTo(1);

        assertThat(findData(1L)).isPresent()
                .hasValueSatisfying(commentLike ->
                        assertThat(commentLike.getStatus()).isEqualTo(LikeStatus.DISLIKE.name())
                );
    }
}