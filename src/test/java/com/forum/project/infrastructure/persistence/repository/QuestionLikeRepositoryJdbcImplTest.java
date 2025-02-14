package com.forum.project.infrastructure.persistence.repository;

import com.forum.project.domain.like.vo.LikeStatus;
import com.forum.project.domain.like.entity.QuestionLike;
import com.forum.project.domain.like.repository.QuestionLikeRepository;
import com.forum.project.infrastructure.persistence.JdbcTestUtils;
import com.forum.project.infrastructure.persistence.key.QuestionLikeKey;
import com.forum.project.infrastructure.persistence.queries.QuestionLikeQueries;
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
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@Slf4j
@JdbcTest
@ActiveProfiles("test")
class QuestionLikeRepositoryJdbcImplTest {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private QuestionLikeRepository questionLikeRepository;

    @BeforeEach
    void setUp() {
        questionLikeRepository = new QuestionLikeRepositoryJdbcImpl(jdbcTemplate);
        JdbcTestUtils.dropTable(jdbcTemplate, "question_likes");
        JdbcTestUtils.createTable(jdbcTemplate, "question_likes",
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "question_id BIGINT NOT NULL, " +
                "status VARCHAR(255), " +
                "ip_address VARCHAR(255), " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ");
    }

    private void insertTestData(Long userId, String status, Long questionId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("status", status)
                .addValue("ipAddress", "192.168.0.1")
                .addValue("questionId", questionId);

        jdbcTemplate.update(QuestionLikeQueries.insertAndReturnGeneratedKeys(), params);
    }

    private QuestionLike createQuestionLike(Long userId, String status, Long questionId) {
        return QuestionLike.builder()
                .userId(userId)
                .questionId(questionId)
                .status(status)
                .ipAddress("192.168.0.1")
                .build();
    }

    private Optional<QuestionLike> findData(Long id) {
        return JdbcTestUtils.findData(jdbcTemplate, QuestionLikeQueries.findById(), id, QuestionLike.class);
    }

    @Test
    void insertAndReturnGeneratedKeys() {
        Timestamp expectedTimestamp = Timestamp.valueOf(LocalDateTime.now());
        Map<String, Object> result = questionLikeRepository.insertAndReturnGeneratedKeys(
                createQuestionLike(1L, LikeStatus.LIKE.name(), 1L));

        assertThat(result).hasSize(QuestionLikeKey.getKeys().length);

        assertThat((Long) result.get(QuestionLikeKey.ID)).isEqualTo(1L);
        assertThat((Timestamp) result.get(QuestionLikeKey.CREATED_DATE)).isAfter(expectedTimestamp);
    }

    @Test
    void existsByQuestionIdAndUserId() {
        insertTestData(1L, LikeStatus.LIKE.name(), 100L);

        boolean result = questionLikeRepository.existsByQuestionIdAndUserId(100L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    void existsByQuestionIdAndUserId_notExists() {
        boolean result = questionLikeRepository.existsByQuestionIdAndUserId(100L, 1L);

        assertThat(result).isFalse();
    }

    @Test
    void findByQuestionIdAndUserId() {
        insertTestData(1L, LikeStatus.LIKE.name(), 100L);

        Optional<QuestionLike> result = questionLikeRepository.findByQuestionIdAndUserId(100L, 1L);

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(questionLike -> {
                    assertThat(questionLike.getQuestionId()).isEqualTo(100L);
                    assertThat(questionLike.getUserId()).isOne();
                    assertThat(questionLike.getStatus()).isEqualTo(LikeStatus.LIKE.name());
                    assertThat(questionLike.getIpAddress()).isEqualTo("192.168.0.1");
                });
    }

    @Test
    void findByQuestionIdAndUserId_notExists() {
        Optional<QuestionLike> result = questionLikeRepository.findByQuestionIdAndUserId(100L, 1L);

        assertThat(result).isEmpty();
    }

    @Test
    void findById() {
        insertTestData(1L, LikeStatus.LIKE.name(), 1L);

        Optional<QuestionLike> result = questionLikeRepository.findById(1L);

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(questionLike -> {
                    assertThat(questionLike.getUserId()).isOne();
                    assertThat(questionLike.getStatus()).isEqualTo(LikeStatus.LIKE.name());
                    assertThat(questionLike.getQuestionId()).isEqualTo(1L);
                });
    }

    @Test
    void findById_notExists() {
        Optional<QuestionLike> result = questionLikeRepository.findById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void deleteByQuestionIdAndUserId() {
        insertTestData(1L, LikeStatus.LIKE.name(), 1L);
        assertThat(findData(1L)).isPresent();

        questionLikeRepository.deleteByQuestionIdAndUserId(1L, 1L);

        assertThat(findData(1L)).isEmpty();
        assertThatCode(() -> questionLikeRepository.deleteByQuestionIdAndUserId(999L, 999L)).doesNotThrowAnyException();
    }
}