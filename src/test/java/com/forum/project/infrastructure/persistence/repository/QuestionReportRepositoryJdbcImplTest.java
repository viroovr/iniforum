package com.forum.project.infrastructure.persistence.repository;

import com.forum.project.domain.report.entity.QuestionReport;
import com.forum.project.domain.report.repository.QuestionReportRepository;
import com.forum.project.domain.report.vo.ReportStatus;
import com.forum.project.infrastructure.persistence.JdbcTestUtils;
import com.forum.project.infrastructure.persistence.key.QuestionReportKey;
import com.forum.project.infrastructure.persistence.queries.QuestionReportQueries;
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
        JdbcTestUtils.dropTable(jdbcTemplate, "question_reports");
        JdbcTestUtils.createTable(jdbcTemplate,"question_reports",
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "question_id BIGINT NOT NULL, " +
                "reason VARCHAR(500) NOT NULL, " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "status ENUM('PENDING', 'IN_PROGRESS', 'RESOLVED', 'REJECTED') DEFAULT 'PENDING', " +
                "is_resolved BOOLEAN DEFAULT FALSE ");
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
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("questionId", questionId)
                .addValue("reason", "validReason")
                .addValue("status", status);
        jdbcTemplate.update(QuestionReportQueries.insertAndReturnGeneratedKeys(), params);
    }

    private Optional<QuestionReport> findData(Long id) {
        return JdbcTestUtils.findData(jdbcTemplate, QuestionReportQueries.findById(), id, QuestionReport.class);
    }

    @Test
    void insertAndReturnGeneratedKeys() {
        Map<String, Object> generatedKeys = questionReportRepository.insertAndReturnGeneratedKeys(
                createQuestionReport(1L, 1L, ReportStatus.PENDING.name()));

        assertThat(generatedKeys).hasSize(QuestionReportKey.getKeys().length);

        assertThat((Long) generatedKeys.get(QuestionReportKey.ID)).isOne();
        assertThat(((Timestamp) generatedKeys.get(QuestionReportKey.CREATED_DATE)).toInstant())
                .isBeforeOrEqualTo(Timestamp.valueOf(LocalDateTime.now()).toInstant());
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

        Optional<QuestionReport> result = questionReportRepository.findById(1L);

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(questionReport -> {
                    assertThat(questionReport.getId()).isEqualTo(1L);
                    assertThat(questionReport.getQuestionId()).isEqualTo(1L);
                    assertThat(questionReport.getUserId()).isEqualTo(1L);
                    assertThat(questionReport.getStatus()).isEqualTo(ReportStatus.PENDING.name());
                });
    }

    @Test
    void findById_notExists() {
        Optional<QuestionReport> result = questionReportRepository.findById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void findById_givenStatusResolved() {
        insertTestData(1L, 1L, ReportStatus.RESOLVED.name());

        Optional<QuestionReport> result = questionReportRepository.findById(1L);

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(questionReport -> {
                    assertThat(questionReport.getQuestionId()).isEqualTo(1L);
                    assertThat(questionReport.getUserId()).isEqualTo(1L);
                    assertThat(questionReport.getStatus()).isEqualTo(ReportStatus.RESOLVED.name());
                });
    }

    @Test
    void findAllByUserId() {
        insertTestData(1L, 1L, ReportStatus.PENDING.name());
        insertTestData(1L, 2L, ReportStatus.PENDING.name());
        insertTestData(2L, 1L, ReportStatus.PENDING.name());

        List<QuestionReport> result = questionReportRepository.findAllByUserId(1L);

        assertThat(result)
                .hasSize(2)
                .allSatisfy(report -> assertThat(report.getUserId()).isOne());
    }

    @Test
    void findAllByQuestionId() {
        insertTestData(1L, 1L, ReportStatus.PENDING.name());
        insertTestData(1L, 2L, ReportStatus.PENDING.name());
        insertTestData(2L, 1L, ReportStatus.PENDING.name());

        List<QuestionReport> result = questionReportRepository.findAllByQuestionId(1L);

        assertThat(result)
                .hasSize(2)
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

    @Test
    void delete() {
        insertTestData(1L, 1L, ReportStatus.PENDING.name());
        assertThat(findData(1L)).isPresent();

        questionReportRepository.delete(1L);
        assertThat(findData(1L)).isEmpty();

        assertThatCode(() -> questionReportRepository.delete(999L)).doesNotThrowAnyException();
    }
}