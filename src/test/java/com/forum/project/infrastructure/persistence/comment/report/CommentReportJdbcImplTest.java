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

    @Test
    void insertAndReturnGeneratedKeys() {
        CommentReport commentReport = createCommentReport(1L, 1L, ReportStatus.PENDING.name());
        Timestamp expectedTimestamp = Timestamp.valueOf(LocalDateTime.now());

        Map<String, Object> generatedKeys = commentReportRepository.insertAndReturnGeneratedKeys(commentReport);

        assertThat(generatedKeys).isNotNull();
        assertThat(generatedKeys.size()).isEqualTo(ReportKey.getKeys().length);

        Long generatedId = (Long) generatedKeys.get(ReportKey.ID);
        Timestamp createdDate = (Timestamp) generatedKeys.get(ReportKey.CREATED_DATE);

        assertThat(generatedId).isEqualTo(1L);
        assertThat(createdDate).isNotNull();
        assertThat(DateUtils.timeDifferenceWithinLimit(expectedTimestamp, createdDate)).isTrue();
    }

    @Test
    void existsByCommentIdAndUserId() {
        Long commentId = 1L;
        Long userId = 1L;
        insertTestData(userId, commentId, "testReason", ReportStatus.PENDING.name());

        boolean result = commentReportRepository.existsByCommentIdAndUserId(commentId, userId);

        assertThat(result).isTrue();
    }

    @Test
    void existsByCommentIdAndUserId_NotExists() {
        Long commentId = 1L;
        Long userId = 1L;
        boolean result = commentReportRepository.existsByCommentIdAndUserId(commentId, userId);

        assertThat(result).isFalse();
    }

    @Test
    void countByCommentId() {
        Long commentId = 1L;
        insertTestData(1L, commentId, "testReason", ReportStatus.PENDING.name());
        insertTestData(2L, commentId, "testReason", ReportStatus.PENDING.name());

        Long result = commentReportRepository.countByCommentId(commentId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(2L);
    }

    @Test
    void findAllByCommentId() {
        Long commentId = 1L;
        insertTestData(1L, commentId, "testReason", ReportStatus.PENDING.name());
        insertTestData(2L, commentId, "testReason", ReportStatus.PENDING.name());
        insertTestData(3L, 2L, "testReason", ReportStatus.PENDING.name());

        List<CommentReport> result = commentReportRepository.findAllByCommentId(commentId);
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2L);
        assertThat(result).extracting("userId", "commentId")
                .containsExactlyInAnyOrder(
                        tuple(1L, commentId),
                        tuple(2L, commentId)
                );
    }

    @Test
    void findAllByUserId() {
        Long userId = 1L;
        insertTestData(userId, 1L, "testReason", ReportStatus.PENDING.name());
        insertTestData(userId, 2L, "testReason", ReportStatus.PENDING.name());
        insertTestData(2L, 2L, "testReason", ReportStatus.PENDING.name());

        List<CommentReport> result = commentReportRepository.findAllByUserId(userId);
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2L);
        assertThat(result).extracting("userId", "commentId")
                .containsExactlyInAnyOrder(
                        tuple(userId, 1L),
                        tuple(userId, 2L)
                );
    }

    @Test
    void findById() {
        Long id = 1L;
        insertTestData(1L, 1L, "testReason", ReportStatus.PENDING.name());

        Optional<CommentReport> optionalCommentReport = commentReportRepository.findById(id);

        assertThat(optionalCommentReport).isPresent();
        CommentReport result = optionalCommentReport.get();

        assertThat(result.getUserId()).isOne();
        assertThat(result.getCommentId()).isOne();
        assertThat(result.getReason()).isEqualTo("testReason");
        assertThat(result.getStatus()).isEqualTo(ReportStatus.PENDING.name());
        assertThat(result.getCreatedDate()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}