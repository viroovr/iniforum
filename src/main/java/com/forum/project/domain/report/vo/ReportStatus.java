package com.forum.project.domain.report.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportStatus {
    PENDING("Pending"), // 대기 중
    IN_PROGRESS("In Progress"), // 진행 중
    RESOLVED("Resolved"), // 해결됨
    REJECTED("Rejected"); // 거절됨

    private final String status;

    public static ReportStatus fromString(String status) {
        for (ReportStatus reportStatus : ReportStatus.values()) {
            if (reportStatus.status.equalsIgnoreCase(status)) {
                return reportStatus;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}
