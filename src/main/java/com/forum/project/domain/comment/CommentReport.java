package com.forum.project.domain.comment;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentReport {
    private Long id;
    private Long commentId;
    private Long userId;  // 신고한 사용자 ID
    private String reason;  // 신고 사유
    private LocalDateTime reportDate;  // 신고 날짜
    private boolean isResolved;

    public void initialize(Long commentId, Long userId, String reason) {
        this.commentId = commentId;
        this.userId = userId;
        this.reason = reason;
        this.reportDate = LocalDateTime.now();
        this.isResolved = false;
    }

    public void markAsResolved() {
        this.isResolved = true;
    }

    public void validateReason() {
        if (this.reason == null || this.reason.trim().isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_COMMENT_REPORT);
        }
        List<String> validReasons = List.of("스팸", "욕설", "부적절한 내용");
        if (!validReasons.contains(this.reason)) {
            throw new ApplicationException(ErrorCode.INVALID_COMMENT_REPORT);
        }
    }
}