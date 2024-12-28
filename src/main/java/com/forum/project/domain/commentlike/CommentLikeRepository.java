package com.forum.project.domain.commentlike;

public interface CommentLikeRepository {
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);
    CommentLike save(CommentLike commentLike);
}
