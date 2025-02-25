package com.forum.project.domain.report.dto;

import com.forum.project.domain.report.vo.ReportReason;
import com.forum.project.domain.report.vo.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentReportCreateDto {
    private Long userId;
    private Long commentId;
    private ReportReason reason;
    private ReportStatus status;
    private String details;
}
