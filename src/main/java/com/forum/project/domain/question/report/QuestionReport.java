package com.forum.project.domain.question.report;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.common.utils.DateUtil;
import com.forum.project.domain.report.BaseReport;
import com.forum.project.domain.report.ReportKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

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

    @Override
    public void setKeys(Map<String, Object> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Keys map cannot be null");
        }

        setId((Long) keys.get(ReportKey.ID));
        setCreatedDate(DateUtil.convertToLocalDateTime(keys.get(ReportKey.CREATED_DATE)));
    }
}
