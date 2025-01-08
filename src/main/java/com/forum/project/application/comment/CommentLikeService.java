package com.forum.project.application.comment;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.comment.Comment;
import com.forum.project.domain.commentlike.CommentLike;
import com.forum.project.domain.commentlike.CommentLikeRepository;
import com.forum.project.domain.commentlike.CommentLikeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;

    public void addLikeComment(Long commentId, Long userId) {
        if(commentLikeRepository.existsByCommentIdAndUserId(commentId, userId))
            throw new ApplicationException(ErrorCode.LIKE_ALREADY_EXISTS);

        commentLikeRepository.save(CommentLike.builder()
                .userId(userId)
                .status(CommentLikeStatus.LIKE.name())
                .commentId(commentId).build());
    }

    public void addDislikeComment(Long commentId, Long userId) {
        if(commentLikeRepository.existsByCommentIdAndUserId(commentId, userId))
            throw new ApplicationException(ErrorCode.LIKE_ALREADY_EXISTS);

        commentLikeRepository.save(CommentLike.builder()
                .userId(userId)
                .status(CommentLikeStatus.DISLIKE.name())
                .commentId(commentId).build());
    }
}
