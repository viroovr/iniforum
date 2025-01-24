package com.forum.project.infrastructure.persistence.question;

import com.forum.project.common.utils.DateUtil;
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
import static org.junit.jupiter.api.Assertions.*;

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
        String insertSql = QuestionLikeQueries.INSERT;
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

    @Test
    void testInsertAndReturnGeneratedKeysQuestionLike_success() {
        QuestionLike questionLike = createQuestionLike(1L, LikeStatus.LIKE.name(), 1L);
        Timestamp expectedTimestamp = Timestamp.valueOf(LocalDateTime.now());

        Map<String, Object> generatedKeys = questionLikeRepository.insertAndReturnGeneratedKeys(questionLike);

        assertThat(generatedKeys).isNotNull();
        assertThat(generatedKeys.size()).isEqualTo(QuestionLikeKey.getKeys().length);

        Long generatedId = (Long) generatedKeys.get(QuestionLikeKey.ID);
        Timestamp createdDate = (Timestamp) generatedKeys.get(QuestionLikeKey.CREATED_DATE);

        assertThat(generatedId).isEqualTo(1L);
        assertThat(createdDate).isNotNull();
        assertThat(DateUtil.timeDifferenceWithinLimit(expectedTimestamp, createdDate)).isTrue();
    }

    @Test
    void testExistsByQuestionIdAndUserId_success() {
        insertTestData(1L, LikeStatus.LIKE.name(), 100L);

        boolean exists = questionLikeRepository.existsByQuestionIdAndUserId(100L, 1L);

        assertTrue(exists);
    }

    @Test
    void testExistsByQuestionIdAndUserId_WhenRecordDoesNotExist_ReturnFalse() {
        boolean exists = questionLikeRepository.existsByQuestionIdAndUserId(999L, 1L);

        assertFalse(exists);
    }

    @Test
    void testFindByQuestionIdAndUserId_success() {
        insertTestData(1L, LikeStatus.LIKE.name(), 100L);

        Optional<QuestionLike> result = questionLikeRepository.findByQuestionIdAndUserId(100L, 1L);
        assertThat(result).isPresent();
        QuestionLike questionLike = result.get();

        assertNotNull(questionLike);
        assertEquals(1L, questionLike.getUserId());
        assertEquals(LikeStatus.LIKE.name(), questionLike.getStatus());
        assertEquals("192.168.0.1", questionLike.getIpAddress());
        assertEquals(100L, questionLike.getQuestionId());
    }

    @Test
    void testDeleteQuestionLike() {
        Long userId = 1L;
        Long questionId = 1L;

        insertTestData(userId, LikeStatus.LIKE.name(), questionId);

        questionLikeRepository.delete(questionId, userId);

        String sql = QuestionLikeQueries.FIND_BY_QUESTION_ID_AND_USER_ID;
        SqlParameterSource source = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("questionId", questionId);

        List<QuestionLike> result = jdbcTemplate.query(sql, source, new BeanPropertyRowMapper<>(QuestionLike.class));

        assertEquals(0, result.size());
    }

}