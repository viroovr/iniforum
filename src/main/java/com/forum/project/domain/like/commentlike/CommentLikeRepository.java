package com.forum.project.domain.like.commentlike;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentLikeRepository {
    boolean existsByUserIdAndCommentId(Long userId, Long commentId);
    Map<String, Object> insertAndReturnGeneratedKeys(CommentLike commentLike);

    Optional<CommentLike> findByUserIdAndCommentId(Long commentId, Long userId);

    void delete(Long id);

    List<Long> findCommentIdsByUserIdAndStatus(Long userId, String name);

    int updateStatus(Long id, String status);
}
