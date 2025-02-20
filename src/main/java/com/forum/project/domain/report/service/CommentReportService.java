package com.forum.project.domain.report.service;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.email.service.EmailService;
import com.forum.project.domain.report.entity.CommentReport;
import com.forum.project.domain.report.repository.CommentReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentReportService implements ReportService<CommentReport> {
    private final CommentReportRepository commentReportRepository;
    private final EmailService emailService;

    private static final long REPORT_THRESHOLD = 10;

    @Override
    public void saveReport(Long commentId, Long userId, String reason) {
        if (commentReportRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new ApplicationException(ErrorCode.COMMENT_ALREADY_REPORTED);
        }
        CommentReport report = CommentReport.builder()
                .userId(userId)
                .commentId(commentId)
                .reason(reason).build();
        report.validateReason();
        commentReportRepository.insertAndReturnGeneratedKeys(report);
    }

    @Override
    public void notifyAdminIfHighReports(Long commentId) {
        Long reportCount = commentReportRepository.countByCommentId(commentId);
        if (reportCount >= REPORT_THRESHOLD) {
            emailService.sendEmailToAdmin(
                    "다중 신고 댓글 알림",
                    "Comment ID " + commentId + " 신고가 " + reportCount + "회 이상 접수되었습니다.");
        }
    }

    @Override
    public List<CommentReport> getReportsById(Long commentId) {
        return commentReportRepository.findAllByCommentId(commentId);
    }

    @Override
    public List<CommentReport> getReportsByUserId(Long userId) {
        return commentReportRepository.findAllByUserId(userId);
    }

    @Override
    public void resolveReport(Long reportId) {
        CommentReport report = commentReportRepository.findById(reportId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_REPORT_NOT_FOUND));
        report.markAsResolved();
        commentReportRepository.insertAndReturnGeneratedKeys(report);
    }
}
