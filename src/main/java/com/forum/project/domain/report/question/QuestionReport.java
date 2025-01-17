package com.forum.project.domain.report.question;

import com.forum.project.domain.report.BaseReport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class QuestionReport extends BaseReport {
    private Long questionId;

    @Override
    public void validateReason() {
        if (getReason() == null || getReason().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be null or empty.");
        }
    }
}
