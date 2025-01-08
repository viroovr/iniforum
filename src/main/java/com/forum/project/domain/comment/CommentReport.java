package com.forum.project.domain.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    public void initialize(Long commentId, Long userId, String reason) {
        this.commentId = commentId;
        this.userId = userId;
        this.reason = reason;
        this.reportDate = LocalDateTime.now();
    }
}