package com.forum.project.infrastructure.persistence.commentlike;

import com.forum.project.domain.commentlike.CommentLike;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository {
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);
    CommentLike save(CommentLike commentLike);

    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);

    void delete(CommentLike existingLike);

    List<Long> findCommentIdsByUserIdAndStatus(Long userId, String name);

    int update(CommentLike existingLike);
}
