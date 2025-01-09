package com.forum.project.application.comment;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.commentlike.CommentLike;
import com.forum.project.domain.commentlike.CommentLikeRepository;
import com.forum.project.domain.commentlike.CommentLikeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void changeLikeStatus(Long commentId, Long userId, String status) {
        CommentLike existingLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_LIKE_NOT_FOUND));

        if (existingLike.getStatus().equals(status)) {
            throw new ApplicationException(ErrorCode.LIKE_ALREADY_EXISTS);
        }

        existingLike.setStatus(status);
        commentLikeRepository.update(existingLike);
    }

    public void removeLikeOrDislike(Long commentId, Long userId) {
        CommentLike existingLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_LIKE_NOT_FOUND));

        commentLikeRepository.delete(existingLike);
    }

    public List<Long> getUserLikedCommentsByStatus(Long userId, String status) {
        return commentLikeRepository.findCommentIdsByUserIdAndStatus(userId, status);
    }
}