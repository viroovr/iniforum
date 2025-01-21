package com.forum.project.domain.report.question;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.report.BaseReport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
            throw new ApplicationException(ErrorCode.INVALID_REPORT);
        }

        List<String> validReasons = List.of("스팸", "욕설", "부적절한 내용");
        if (!validReasons.contains(getReason())) {
            throw new ApplicationException(ErrorCode.INVALID_REPORT);
        }
    }
}
