package com.forum.project.application.comment;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.application.email.EmailAdminService;
import com.forum.project.application.report.ReportService;
import com.forum.project.domain.comment.CommentReport;
import com.forum.project.domain.comment.CommentReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentReportService implements ReportService<CommentReport> {
    private final CommentReportRepository commentReportRepository;
    private final EmailAdminService emailAdminService;

    private static final long REPORT_THRESHOLD = 10;

    @Override
    public void saveReport(Long commentId, Long userId, String reason) {
        if (commentReportRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new ApplicationException(ErrorCode.COMMENT_ALREADY_REPORTED);
        }
        CommentReport report = new CommentReport();
        report.initialize(userId, reason, commentId);
        report.validateReason();
        commentReportRepository.save(report);
    }

    @Override
    public void notifyAdminIfHighReports(Long commentId) {
        Long reportCount = commentReportRepository.countByCommentId(commentId);
        if (reportCount >= REPORT_THRESHOLD) {
            emailAdminService.sendEmail(
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
        commentReportRepository.save(report);
    }
}
