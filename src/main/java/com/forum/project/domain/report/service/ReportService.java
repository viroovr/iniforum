package com.forum.project.domain.report.service;

import com.forum.project.core.base.BaseReport;

import java.util.List;

public interface ReportService<T extends BaseReport> {
    void saveReport(Long id, Long userId, String reason);

    void notifyAdminIfHighReports(Long id);

    List<T> getReportsById(Long id);

    List<T> getReportsByUserId(Long id);

    void resolveReport(Long reportId);
}
