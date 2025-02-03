package com.forum.project.application.comment;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.like.LikeStatus;
import com.forum.project.domain.like.commentlike.CommentLike;
import com.forum.project.domain.like.commentlike.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;

    private CommentLike createCommentLike(Long userId, Long commentId, String status) {
        return CommentLike.builder()
                .userId(userId)
                .status(status)
                .commentId(commentId)
                .build();
    }

    public void addLikeComment(Long commentId, Long userId) {
        if(commentLikeRepository.existsByUserIdAndCommentId(userId, commentId))
            throw new ApplicationException(ErrorCode.LIKE_ALREADY_EXISTS);

        commentLikeRepository.insertAndReturnGeneratedKeys(
                createCommentLike(userId, commentId, LikeStatus.LIKE.name()));
    }

    public void addDislikeComment(Long commentId, Long userId) {
        if(commentLikeRepository.existsByUserIdAndCommentId(userId, commentId))
            throw new ApplicationException(ErrorCode.LIKE_ALREADY_EXISTS);

        commentLikeRepository.insertAndReturnGeneratedKeys(
                createCommentLike(userId, commentId, LikeStatus.DISLIKE.name())
        );
    }

    public void changeLikeStatus(Long commentId, Long userId, String status) {
        CommentLike existingLike = commentLikeRepository.findByUserIdAndCommentId(userId, commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.LIKE_NOT_FOUND));

        if (existingLike.getStatus().equals(status)) {
            throw new ApplicationException(ErrorCode.LIKE_ALREADY_EXISTS);
        }

        commentLikeRepository.updateStatus(existingLike.getId(), status);
    }

    public void removeLikeOrDislike(Long commentId, Long userId) {
        CommentLike existingLike = commentLikeRepository.findByUserIdAndCommentId(userId, commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.LIKE_NOT_FOUND));

        commentLikeRepository.delete(existingLike.getId());
    }

    public List<Long> getUserLikedCommentsByStatus(Long userId, String status) {
        return commentLikeRepository.findCommentIdsByUserIdAndStatus(userId, status);
    }
}