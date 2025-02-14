package com.forum.project.infrastructure.persistence.repository;

import com.forum.project.domain.report.entity.CommentReport;
import com.forum.project.domain.report.repository.CommentReportRepository;
import com.forum.project.domain.report.vo.ReportStatus;
import com.forum.project.infrastructure.persistence.JdbcTestUtils;
import com.forum.project.infrastructure.persistence.key.CommentReportKey;
import com.forum.project.infrastructure.persistence.queries.CommentReportQueries;
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
class CommentReportJdbcImplTest {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private CommentReportRepository commentReportRepository;

    @BeforeEach
    void setUp() {
        commentReportRepository = new CommentReportJdbcImpl(jdbcTemplate);
        JdbcTestUtils.dropTable(jdbcTemplate, "comment_reports");
        JdbcTestUtils.createTable(jdbcTemplate, "comment_reports",
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "comment_id BIGINT NOT NULL, " +
                "reason VARCHAR(500) NOT NULL, " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "status ENUM('PENDING', 'IN_PROGRESS', 'RESOLVED', 'REJECTED') DEFAULT 'PENDING', " +
                "is_resolved BOOLEAN DEFAULT FALSE ");
    }

    private CommentReport createCommentReport(Long userId, Long commentId, String status) {
        return CommentReport.builder()
                .userId(userId)
                .commentId(commentId)
                .reason("validReason")
                .status(status)
                .build();
    }

    private void insertTestData(Long userId, Long commentId, String reason, String status) {
        String insertSql = CommentReportQueries.insert();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("commentId", commentId)
                .addValue("reason", reason)
                .addValue("status", status);
        jdbcTemplate.update(insertSql, params);
    }

    private Optional<CommentReport> findData(Long id) {
        return JdbcTestUtils.findData(jdbcTemplate, CommentReportQueries.findById(), id, CommentReport.class);
    }

    @Test
    void insertAndReturnGeneratedKeys() {
        Timestamp expectedTimestamp = Timestamp.valueOf(LocalDateTime.now());
        Map<String, Object> generatedKeys = commentReportRepository.insertAndReturnGeneratedKeys(
                createCommentReport(1L, 1L, ReportStatus.PENDING.name()));

        assertThat(generatedKeys).hasSize(CommentReportKey.getKeys().length);

        assertThat((Long) generatedKeys.get(CommentReportKey.ID)).isEqualTo(1L);
        assertThat((Timestamp) generatedKeys.get(CommentReportKey.CREATED_DATE)).isAfter(expectedTimestamp);
    }

    @Test
    void existsByCommentIdAndUserId() {
        insertTestData(1L, 1L, "testReason", ReportStatus.PENDING.name());

        boolean result = commentReportRepository.existsByCommentIdAndUserId(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    void existsByCommentIdAndUserId_NotExists() {
        boolean result = commentReportRepository.existsByCommentIdAndUserId(1L, 1L);

        assertThat(result).isFalse();
    }

    @Test
    void countByCommentId() {
        insertTestData(1L, 1L, "testReason", ReportStatus.PENDING.name());
        insertTestData(2L, 1L, "testReason", ReportStatus.PENDING.name());

        Long result = commentReportRepository.countByCommentId(1L);

        assertThat(result).isEqualTo(2L);
    }

    @Test
    void findAllByCommentId() {
        insertTestData(1L, 1L, "testReason", ReportStatus.PENDING.name());
        insertTestData(2L, 1L, "testReason", ReportStatus.PENDING.name());
        insertTestData(3L, 2L, "testReason", ReportStatus.PENDING.name());

        List<CommentReport> result = commentReportRepository.findAllByCommentId(1L);

        assertThat(result)
                .hasSize(2)
                .extracting(CommentReport::getCommentId)
                .containsOnly(1L);
    }

    @Test
    void findAllByUserId() {
        insertTestData(1L, 1L, "testReason", ReportStatus.PENDING.name());
        insertTestData(1L, 2L, "testReason", ReportStatus.PENDING.name());
        insertTestData(2L, 2L, "testReason", ReportStatus.PENDING.name());

        List<CommentReport> result = commentReportRepository.findAllByUserId(1L);

        assertThat(result)
                .hasSize(2)
                .extracting(CommentReport::getUserId)
                .containsOnly(1L);
    }

    @Test
    void findById() {
        insertTestData(1L, 1L, "testReason", ReportStatus.PENDING.name());

        Optional<CommentReport> result = commentReportRepository.findById(1L);

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(commentReport -> {
                    assertThat(commentReport.getId()).isOne();
                    assertThat(commentReport.getUserId()).isOne();
                    assertThat(commentReport.getCommentId()).isOne();
                });
    }

    @Test
    void findById_notExists() {
        Optional<CommentReport> result = commentReportRepository.findById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void delete() {
        insertTestData(1L, 1L, "testReason", ReportStatus.PENDING.name());
        assertThat(findData(1L)).isPresent();

        commentReportRepository.delete(1L);
        assertThat(findData(1L)).isEmpty();

        assertThatCode(() -> commentReportRepository.delete(999L)).doesNotThrowAnyException();
    }
}