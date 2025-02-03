package com.forum.project.infrastructure.persistence.question.report;

import com.forum.project.common.utils.DateUtils;
import com.forum.project.domain.question.report.QuestionReport;
import com.forum.project.domain.question.report.QuestionReportKey;
import com.forum.project.domain.question.report.QuestionReportRepository;
import com.forum.project.domain.report.ReportStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
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

@JdbcTest
@ActiveProfiles("test")
@Slf4j
class QuestionReportRepositoryJdbcImplTest {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private QuestionReportRepository questionReportRepository;

    @BeforeEach
    void setUp() {
        questionReportRepository = new QuestionReportRepositoryJdbcImpl(jdbcTemplate);
        jdbcTemplate.getJdbcTemplate().execute("CREATE TABLE question_reports (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "question_id BIGINT NOT NULL, " +
                "reason VARCHAR(500) NOT NULL, " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "status ENUM('PENDING', 'IN_PROGRESS', 'RESOLVED', 'REJECTED') DEFAULT 'PENDING', " +
                "is_resolved BOOLEAN DEFAULT FALSE" +
                ");");
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.getJdbcTemplate().execute("DROP TABLE IF EXISTS question_reports");
    }

    private QuestionReport createQuestionReport(Long userId, Long questionId, String status) {
        return QuestionReport.builder()
                .userId(userId)
                .questionId(questionId)
                .reason("validReason")
                .status(status)
                .build();
    }

    private void insertTestData(Long userId, Long questionId, String status) {
        String sql = QuestionReportQueries.insertAndReturnGeneratedKeys();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("questionId", questionId)
                .addValue("reason", "validReason")
                .addValue("status", status);
        jdbcTemplate.update(sql, params);
    }

    @Test
    void insertAndReturnGeneratedKeys() {
        QuestionReport report = createQuestionReport(1L, 1L, ReportStatus.PENDING.name());
        Timestamp expectedTimeStamp = Timestamp.valueOf(LocalDateTime.now());

        Map<String, Object> generatedKeys = questionReportRepository.insertAndReturnGeneratedKeys(report);
        assertThat(generatedKeys).isNotNull();

        Long id = (Long) generatedKeys.get(QuestionReportKey.ID);
        Timestamp createdDate = (Timestamp) generatedKeys.get(QuestionReportKey.CREATED_DATE);

        assertThat(id).isOne();
        assertThat(DateUtils.timeDifferenceWithinLimit(expectedTimeStamp, createdDate)).isTrue();
    }

    @Test
    void existsByQuestionIdAndUserId() {
        insertTestData(1L, 1L, ReportStatus.PENDING.name());

        boolean result = questionReportRepository.existsByQuestionIdAndUserId(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    void existsByQuestionIdAndUserId_fail() {
        boolean result = questionReportRepository.existsByQuestionIdAndUserId(1L, 1L);

        assertThat(result).isFalse();
    }

    @Test
    void findById() {
        insertTestData(1L, 1L, ReportStatus.PENDING.name());

        Optional<QuestionReport> optionalQuestionReport = questionReportRepository.findById(1L);
        assertThat(optionalQuestionReport).isNotEmpty();

        QuestionReport result = optionalQuestionReport.get();

        assertThat(result)
                .extracting(QuestionReport::getQuestionId, QuestionReport::getUserId, QuestionReport::getStatus)
                .containsExactly(1L, 1L, ReportStatus.PENDING.name());
    }

    @Test
    void findById_givenStatusResolved() {
        insertTestData(1L, 1L, ReportStatus.RESOLVED.name());

        Optional<QuestionReport> optionalQuestionReport = questionReportRepository.findById(1L);
        assertThat(optionalQuestionReport).isNotEmpty();

        QuestionReport result = optionalQuestionReport.get();

        assertThat(result)
                .extracting(QuestionReport::getQuestionId, QuestionReport::getUserId, QuestionReport::getStatus)
                .containsExactly(1L, 1L, ReportStatus.RESOLVED.name());
    }

    @Test
    void findAllByUserId() {
        insertTestData(1L, 1L, ReportStatus.PENDING.name());
        insertTestData(1L, 2L, ReportStatus.PENDING.name());
        insertTestData(2L, 1L, ReportStatus.PENDING.name());

        List<QuestionReport> result = questionReportRepository.findAllByUserId(1L);
        assertThat(result).hasSize(2);

        assertThat(result)
                .allSatisfy(report -> assertThat(report.getUserId()).isOne());
    }

    @Test
    void findAllByQuestionId() {
        insertTestData(1L, 1L, ReportStatus.PENDING.name());
        insertTestData(1L, 2L, ReportStatus.PENDING.name());
        insertTestData(2L, 1L, ReportStatus.PENDING.name());

        List<QuestionReport> result = questionReportRepository.findAllByQuestionId(1L);
        assertThat(result).hasSize(2);

        assertThat(result)
                .allSatisfy(report -> assertThat(report.getQuestionId()).isOne());
    }

    @Test
    void countByQuestionId() {
        insertTestData(1L, 1L, ReportStatus.PENDING.name());
        insertTestData(2L, 1L, ReportStatus.PENDING.name());
        insertTestData(3L, 1L, ReportStatus.PENDING.name());
        insertTestData(3L, 2L, ReportStatus.PENDING.name());

        Long result = questionReportRepository.countByQuestionId(1L);
        assertThat(result).isEqualTo(3L);
    }
}