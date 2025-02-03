package com.forum.project.domain.comment.report;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.common.utils.DateUtils;
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
public class CommentReport extends BaseReport {
    private Long commentId;

    public void setKeys(Map<String, Object> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Keys map cannot be null");
        }

        this.setId((Long) keys.get(ReportKey.ID));
        this.setCreatedDate(DateUtils.convertToLocalDateTime(keys.get(ReportKey.CREATED_DATE)));
    }

    public void validateReason() {
        if (getReason() == null || getReason().trim().isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_REPORT);
        }
        List<String> validReasons = List.of("스팸", "욕설", "부적절한 내용");
        if (!validReasons.contains(getReason())) {
            throw new ApplicationException(ErrorCode.INVALID_REPORT);
        }
    }
}