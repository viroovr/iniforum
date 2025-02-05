package com.forum.project.infrastructure.persistence.question.like;

import com.forum.project.domain.like.LikeStatus;
import com.forum.project.domain.question.like.QuestionLike;
import com.forum.project.domain.question.like.QuestionLikeKey;
import com.forum.project.domain.question.like.QuestionLikeRepository;
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
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        jdbcTemplate.getJdbcTemplate().execute("CREATE TABLE question_likes (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "question_id BIGINT NOT NULL, " +
                "status VARCHAR(255), " +
                "ip_address VARCHAR(255), " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");");
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.getJdbcTemplate().execute("DROP TABLE IF EXISTS question_likes;");
    }

    private void insertTestData(Long userId, String status, Long questionId) {
        String insertSql = QuestionLikeQueries.insertAndReturnGeneratedKeys();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("status", status)
                .addValue("ipAddress", "192.168.0.1")
                .addValue("questionId", questionId);

        jdbcTemplate.update(insertSql, params);
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
        String sql = QuestionLikeQueries.findById();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            QuestionLike result = jdbcTemplate.queryForObject(sql, params,
                    new BeanPropertyRowMapper<>(QuestionLike.class));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Test
    void insertAndReturnGeneratedKeys() {
        QuestionLike questionLike = createQuestionLike(1L, LikeStatus.LIKE.name(), 1L);
        Timestamp expectedTimestamp = Timestamp.valueOf(LocalDateTime.now());

        Map<String, Object> result = questionLikeRepository.insertAndReturnGeneratedKeys(questionLike);

        assertThat(result).hasSize(QuestionLikeKey.getKeys().length);

        assertThat((Long) result.get(QuestionLikeKey.ID)).isEqualTo(1L);
        assertThat((Timestamp) result.get(QuestionLikeKey.CREATED_DATE)).isAfter(expectedTimestamp);
    }

    @Test
    void existsByQuestionIdAndUserId() {
        insertTestData(1L, LikeStatus.LIKE.name(), 100L);

        boolean exists = questionLikeRepository.existsByQuestionIdAndUserId(100L, 1L);

        assertTrue(exists);
    }

    @Test
    void existsByQuestionIdAndUserId_notExists() {
        boolean exists = questionLikeRepository.existsByQuestionIdAndUserId(100L, 1L);

        assertFalse(exists);
    }

    @Test
    void findByQuestionIdAndUserId() {
        insertTestData(1L, LikeStatus.LIKE.name(), 100L);

        Optional<QuestionLike> result = questionLikeRepository.findByQuestionIdAndUserId(100L, 1L);

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(questionLike -> {
                    assertThat(questionLike.getUserId()).isOne();
                    assertThat(questionLike.getStatus()).isEqualTo(LikeStatus.LIKE.name());
                    assertThat(questionLike.getIpAddress()).isEqualTo("192.168.0.1");
                    assertThat(questionLike.getQuestionId()).isEqualTo(100L);
                });
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
    void deleteByQuestionIdAndUserId() {
        insertTestData(1L, LikeStatus.LIKE.name(), 1L);
        assertThat(findData(1L)).isPresent();

        questionLikeRepository.deleteByQuestionIdAndUserId(1L, 1L);

        assertThat(findData(1L)).isEmpty();
        assertThatCode(() -> questionLikeRepository.deleteByQuestionIdAndUserId(999L, 999L)).doesNotThrowAnyException();
    }

}