package com.forum.project.domain.commentlike;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentLike {
    private Long id;
    private Long userId;
    private Long commentId;
    private String status;
    private String ipAddress;

    public void initialize(Long userId, Long commentId) {
        this.userId = userId;
        this.commentId = commentId;
        this.status = CommentLikeStatus.NONE.name();
    }

    public void like() {
        this.status = CommentLikeStatus.LIKE.name();
    }

    public void dislike() {
        this.status = CommentLikeStatus.DISLIKE.name();
    }
}
