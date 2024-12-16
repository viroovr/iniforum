package com.forum.project.domain.repository;

import com.forum.project.domain.entity.Comment;
import com.forum.project.domain.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository {
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);
    CommentLike save(CommentLike commentLike);
}
