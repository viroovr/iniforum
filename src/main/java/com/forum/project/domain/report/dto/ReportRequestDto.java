package com.forum.project.domain.report.dto;

import com.forum.project.domain.report.vo.ReportReason;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {
    @NotBlank
    private ReportReason reason;
    private String details;
}
