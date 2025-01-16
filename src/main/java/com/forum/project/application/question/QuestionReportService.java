package com.forum.project.application.question;

import com.forum.project.application.email.EmailAdminService;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.application.report.ReportService;
import com.forum.project.domain.question.QuestionReport;
import com.forum.project.domain.question.QuestionReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionReportService implements ReportService<QuestionReport> {
    private final QuestionReportRepository questionReportRepository;
    private final EmailAdminService emailAdminService;

    private static final long REPORT_THRESHOLD = 10;

    @Override
    public void saveReport(Long questionId, Long userId, String reason) {
        if (questionReportRepository.existsByIdAndUserId(questionId, userId)) {
            throw new ApplicationException(ErrorCode.COMMENT_ALREADY_REPORTED);
        }
        QuestionReport report = new QuestionReport();
        report.initialize(userId, reason, questionId);
        report.validateReason();
        questionReportRepository.save(report);

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
        questionReportRepository.save(report);
    }
}

