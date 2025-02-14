package com.forum.project.domain.report.service;

import com.forum.project.domain.auth.service.EmailAdminService;
import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.question.validator.QuestionValidator;
import com.forum.project.domain.report.entity.QuestionReport;
import com.forum.project.domain.report.repository.QuestionReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionReportService implements ReportService<QuestionReport> {
    private final QuestionReportRepository questionReportRepository;
    private final QuestionValidator questionValidator;
    private final EmailAdminService emailAdminService;

    private static final long REPORT_THRESHOLD = 10;

    @Override
    public void saveReport(Long questionId, Long userId, String reason) {
        questionValidator.validateQuestion(questionId);

        if (questionReportRepository.existsByQuestionIdAndUserId(questionId, userId)) {
            throw new ApplicationException(ErrorCode.QUESTION_ALREADY_REPORTED);
        }

        QuestionReport report = QuestionReport.builder()
                .questionId(questionId)
                .userId(userId)
                .reason(reason).build();
        report.validateReason();
        questionReportRepository.insertAndReturnGeneratedKeys(report);

        notifyAdminIfHighReports(questionId);
    }

    @Override
    public void notifyAdminIfHighReports(Long questionId) {
        Long reportCount = questionReportRepository.countByQuestionId(questionId);
        if (reportCount >= REPORT_THRESHOLD) {
            emailAdminService.sendEmail(
                    "다중 신고 댓글 알림",
                    "Question ID " + questionId + " 신고가 " + reportCount + "회 이상 접수되었습니다.");
        }
    }

    @Override
    public List<QuestionReport> getReportsById(Long questionId) {
        return questionReportRepository.findAllByQuestionId(questionId);
    }

    @Override
    public List<QuestionReport> getReportsByUserId(Long userId) {
        return questionReportRepository.findAllByUserId(userId);
    }

    @Override
    public void resolveReport(Long reportId) {
        QuestionReport report = questionReportRepository.findById(reportId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.QUESTION_REPORT_NOT_FOUND));
        report.markAsResolved();
        questionReportRepository.insertAndReturnGeneratedKeys(report);
    }
}

