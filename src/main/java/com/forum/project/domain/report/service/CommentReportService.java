package com.forum.project.domain.report.service;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.comment.vo.CommentContext;
import com.forum.project.domain.email.service.EmailService;
import com.forum.project.domain.report.dto.CommentReportCreateDto;
import com.forum.project.domain.report.dto.ReportRequestDto;
import com.forum.project.domain.report.entity.CommentReport;
import com.forum.project.domain.report.mapper.CommentReportDtoMapper;
import com.forum.project.domain.report.repository.CommentReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentReportService {
    private final CommentReportRepository commentReportRepository;
    private final EmailService emailService;

    private static final long REPORT_THRESHOLD = 10;

    private void validateAlreadyReported(CommentContext context) {
        if (commentReportRepository.existsByCommentIdAndUserId(context.commentId(), context.userId()))
            throw new ApplicationException(ErrorCode.COMMENT_ALREADY_REPORTED);
    }

    public void saveReport(CommentContext context, ReportRequestDto dto) {
        validateAlreadyReported(context);
        CommentReportCreateDto createDto = CommentReportDtoMapper.toCommentReportCreateDto(dto, context);
        commentReportRepository.insertAndReturnGeneratedKeys(createDto);
    }

    public void notifyAdminIfHighReports(Long commentId) {
        Long reportCount = commentReportRepository.countByCommentId(commentId);
        if (reportCount >= REPORT_THRESHOLD) {
            emailService.sendEmailToAdmin(
                    "다중 신고 댓글 알림",
                    "Comment ID " + commentId + " 신고가 " + reportCount + "회 이상 접수되었습니다.");
        }
    }

    public List<CommentReport> getReportsById(Long commentId) {
        return commentReportRepository.findAllByCommentId(commentId);
    }

    public List<CommentReport> getReportsByUserId(Long userId) {
        return commentReportRepository.findAllByUserId(userId);
    }

    public void resolveReport(Long reportId) {
        CommentReport report = commentReportRepository.findById(reportId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_REPORT_NOT_FOUND));
        report.markAsResolved();
//        commentReportRepository.insertAndReturnGeneratedKeys(report);
    }
}
