package com.forum.project.domain.comment;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.report.BaseReport;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CommentReport extends BaseReport {
    private Long commentId;

    public void initialize(Long userId, String reason, Long commentId) {
        super.initialize(userId, reason);
        this.commentId = commentId;
    }

    public void validateReason() {
        if (getReason() == null || getReason().trim().isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_COMMENT_REPORT);
        }
        List<String> validReasons = List.of("스팸", "욕설", "부적절한 내용");
        if (!validReasons.contains(getReason())) {
            throw new ApplicationException(ErrorCode.INVALID_COMMENT_REPORT);
        }
    }
}