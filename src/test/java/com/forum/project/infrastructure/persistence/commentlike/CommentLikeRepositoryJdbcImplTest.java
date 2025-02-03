package com.forum.project.infrastructure.persistence.commentlike;

import com.forum.project.common.utils.DateUtil;
import com.forum.project.domain.like.LikeStatus;
import com.forum.project.domain.like.commentlike.CommentLike;
import com.forum.project.domain.like.commentlike.CommentLikeKey;
import com.forum.project.domain.like.commentlike.CommentLikeRepository;
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
        jdbcTemplate.getJdbcTemplate().execute("CREATE TABLE comment_likes (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "comment_id BIGINT NOT NULL, " +
                "status ENUM('LIKE', 'DISLIKE', 'NONE') DEFAULT 'LIKE', " +
                "ip_address VARCHAR(255), " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
                ");");
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.getJdbcTemplate().execute("DROP TABLE IF EXISTS comment_likes");
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
        String sql = "SELECT * FROM comment_likes WHERE id = :id";
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            CommentLike result = jdbcTemplate.queryForObject(sql, params,
                    new BeanPropertyRowMapper<>(CommentLike.class));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Test
    void insertAndReturnGeneratedKeys() {
        CommentLike commentLike = createCommentLike(1L, 1L, LikeStatus.LIKE.name());
        Timestamp expectedTimeStamp = Timestamp.valueOf(LocalDateTime.now());
        Map<String, Object> result = commentLikeRepository.insertAndReturnGeneratedKeys(commentLike);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(CommentLikeKey.getKeys().length);

        Long generatedId = (Long) result.get(CommentLikeKey.ID);
        Timestamp createdDate = (Timestamp) result.get(CommentLikeKey.CREATED_DATE);

        assertThat(generatedId).isEqualTo(1L);
        assertThat(createdDate).isNotNull();
        assertThat(DateUtil.timeDifferenceWithinLimit(expectedTimeStamp, createdDate)).isTrue();
    }

    @Test
    void existsByUserIdAndCommentId() {
        boolean result = commentLikeRepository.existsByUserIdAndCommentId(1L, 1L);

        assertThat(result).isFalse();
    }

    @Test
    void existsByUserIdAndCommentId_fail() {
        insertTestData(1L, 1L, LikeStatus.LIKE.name());

        boolean result = commentLikeRepository.existsByUserIdAndCommentId(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    void findByUserIdAndCommentId() {
        insertTestData(1L, 1L, LikeStatus.LIKE.name());
        Optional<CommentLike> result = commentLikeRepository.findByUserIdAndCommentId(1L, 1L);

        assertThat(result).isPresent();
        CommentLike commentLike = result.get();

        assertThat(commentLike).isNotNull();
        assertThat(commentLike.getCommentId()).isEqualTo(1L);
        assertThat(commentLike.getUserId()).isEqualTo(1L);
        assertThat(commentLike.getStatus()).isEqualTo(LikeStatus.LIKE.name());
    }

    @Test
    void delete() {
        insertTestData(1L, 1L, LikeStatus.LIKE.name());

        commentLikeRepository.delete(1L);

        Optional<CommentLike> result = findData(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void findCommentIdsByUserIdAndStatus() {
        insertTestData(1L, 1L, LikeStatus.LIKE.name());
        insertTestData(1L, 2L, LikeStatus.LIKE.name());
        insertTestData(2L, 1L, LikeStatus.LIKE.name());
        insertTestData(1L, 3L, LikeStatus.DISLIKE.name());

        List<Long> result = commentLikeRepository.findCommentIdsByUserIdAndStatus(1L, LikeStatus.LIKE.name());

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void updateStatus() {
        insertTestData(1L, 1L, LikeStatus.LIKE.name());

        int result = commentLikeRepository.updateStatus(1L, LikeStatus.DISLIKE.name());
        assertThat(result).isEqualTo(1);

        Optional<CommentLike> likeOptional = findData(1L);
        assertThat(likeOptional).isPresent();

        CommentLike commentLike = likeOptional.get();
        assertThat(commentLike.getStatus()).isEqualTo(LikeStatus.DISLIKE.name());
    }
}