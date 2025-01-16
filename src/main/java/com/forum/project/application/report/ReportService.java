package com.forum.project.application.report;

import com.forum.project.domain.question.QuestionReport;
import com.forum.project.domain.report.BaseReport;

import java.util.List;

public interface ReportService<T extends BaseReport> {
    void saveReport(Long id, Long userId, String reason);

    void notifyAdminIfHighReports(Long id);

    List<T> getReportsById(Long id);

    List<T> getReportsByUserId(Long id);

    void resolveReport(Long reportId);
}
