package com.forum.project.infrastructure.persistence.comment.report;

import com.forum.project.common.utils.DateUtils;
import com.forum.project.domain.comment.report.CommentReport;
import com.forum.project.domain.comment.report.CommentReportRepository;
import com.forum.project.domain.report.ReportKey;
import com.forum.project.domain.report.ReportStatus;
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

import static org.assertj.core.api.Assertions.*;

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

        jdbcTemplate.getJdbcTemplate().execute("CREATE TABLE comment_reports (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "comment_id BIGINT NOT NULL, " +
                "reason VARCHAR(500) NOT NULL, " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "status ENUM('PENDING', 'IN_PROGRESS', 'RESOLVED', 'REJECTED') DEFAULT 'PENDING', " +
                "is_resolved BOOLEAN DEFAULT FALSE " +
                ");");
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.getJdbcTemplate().execute("DROP TABLE IF EXISTS comment_reports");
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
        String sql = CommentReportQueries.findById();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            CommentReport result = jdbcTemplate.queryForObject(sql, params,
                    new BeanPropertyRowMapper<>(CommentReport.class));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Test
    void insertAndReturnGeneratedKeys() {
        Map<String, Object> generatedKeys = commentReportRepository.insertAndReturnGeneratedKeys(
                createCommentReport(1L, 1L, ReportStatus.PENDING.name()));

        assertThat(generatedKeys).hasSize(ReportKey.getKeys().length);

        assertThat((Long) generatedKeys.get(ReportKey.ID)).isEqualTo(1L);
        assertThat((Timestamp) generatedKeys.get(ReportKey.CREATED_DATE)).isBefore(Timestamp.valueOf(LocalDateTime.now()));
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
                    assertThat(commentReport.getUserId()).isOne();
                    assertThat(commentReport.getCommentId()).isOne();
                    assertThat(commentReport.getReason()).isEqualTo("testReason");
                    assertThat(commentReport.getStatus()).isEqualTo(ReportStatus.PENDING.name());
                    assertThat(commentReport.getCreatedDate()).isBeforeOrEqualTo(LocalDateTime.now());
                });
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