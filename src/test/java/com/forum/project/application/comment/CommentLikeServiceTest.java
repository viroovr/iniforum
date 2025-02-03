package com.forum.project.application.comment;

import com.forum.project.domain.like.LikeStatus;
import com.forum.project.domain.like.commentlike.CommentLike;
import com.forum.project.domain.like.commentlike.CommentLikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceTest {
    @Mock
    private CommentLikeRepository commentLikeRepository;

    @InjectMocks
    private CommentLikeService commentLikeService;

    @Test
    void testAddLikeComment_success() {
        Long commentId = 1L;
        Long userId = 1L;

        when(commentLikeRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(false);
        when(commentLikeRepository.insertAndReturnGeneratedKeys(any(CommentLike.class)))
                .thenReturn(Collections.emptyMap());

        commentLikeService.addLikeComment(commentId, userId);

        verify(commentLikeRepository).existsByUserIdAndCommentId(userId, commentId);
        verify(commentLikeRepository).insertAndReturnGeneratedKeys(any(CommentLike.class));
    }

    @Test
    void testDisLikeComment_success() {
        Long commentId = 1L;
        Long userId = 1L;

        when(commentLikeRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(false);
        when(commentLikeRepository.insertAndReturnGeneratedKeys(any(CommentLike.class)))
                .thenReturn(Collections.emptyMap());

        commentLikeService.addDislikeComment(commentId, userId);

        verify(commentLikeRepository).existsByUserIdAndCommentId(userId, commentId);
        verify(commentLikeRepository).insertAndReturnGeneratedKeys(any(CommentLike.class));
    }

    @Test
    void testChangeLikeStatus_success() {
        Long commentId = 1L;
        Long userId = 1L;
        String status = LikeStatus.LIKE.name();
        CommentLike commentLike = CommentLike.builder()
                .id(1L)
                .commentId(commentId)
                .userId(userId)
                .build();

        when(commentLikeRepository.findByUserIdAndCommentId(userId, commentId))
                .thenReturn(Optional.of(commentLike));
        when(commentLikeRepository.updateStatus(1L, status)).thenReturn(1);

        commentLikeService.changeLikeStatus(commentId, userId, status);
        verify(commentLikeRepository).findByUserIdAndCommentId(commentId, userId);
        verify(commentLikeRepository).updateStatus(1L, status);
    }

    @Test
    void testRemoveLikeOrDislike_success() {
        Long commentId = 1L;
        Long userId = 1L;
        CommentLike commentLike = CommentLike.builder()
                .id(1L)
                .commentId(commentId)
                .userId(userId)
                .build();

        when(commentLikeRepository.findByUserIdAndCommentId(commentId, userId))
                .thenReturn(Optional.ofNullable(commentLike));
        doNothing().when(commentLikeRepository).delete(commentLike.getId());

        commentLikeService.removeLikeOrDislike(commentId, userId);

        verify(commentLikeRepository).delete(1L);
    }

    @Test
    void testGetUserLikedComments_success() {
        Long userId = 1L;
        String status = LikeStatus.LIKE.name();
        List<Long> longList = List.of(1L, 2L);
        when(commentLikeRepository.findCommentIdsByUserIdAndStatus(userId, status))
                .thenReturn(longList);

        List<Long> response = commentLikeService.getUserLikedCommentsByStatus(userId, status);

        assertNotNull(response);
        assertEquals(1L, response.get(0));
    }
}