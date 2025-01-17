package com.forum.project.application.comment;

import com.forum.project.domain.like.commentlike.CommentLike;
import com.forum.project.domain.like.commentlike.CommentLikeRepository;
import com.forum.project.domain.like.LikeStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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

        when(commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(false);
        ArgumentCaptor<CommentLike> captor = ArgumentCaptor.forClass(CommentLike.class);
        when(commentLikeRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        commentLikeService.addLikeComment(commentId, userId);
        CommentLike commentLike = captor.getValue();

        assertNotNull(commentLike);
        assertEquals(userId, commentLike.getUserId());
        assertEquals(LikeStatus.LIKE.name(), commentLike.getStatus());
        assertEquals(commentId, commentLike.getCommentId());
    }

    @Test
    void testDisLikeComment_success() {
        Long commentId = 1L;
        Long userId = 1L;

        when(commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(false);
        ArgumentCaptor<CommentLike> captor = ArgumentCaptor.forClass(CommentLike.class);
        when(commentLikeRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        commentLikeService.addDislikeComment(commentId, userId);
        CommentLike commentLike = captor.getValue();

        assertNotNull(commentLike);
        assertEquals(userId, commentLike.getUserId());
        assertEquals(LikeStatus.DISLIKE.name(), commentLike.getStatus());
        assertEquals(commentId, commentLike.getCommentId());
    }

    @Test
    void testChangeLikeStatus_success() {
        Long commentId = 1L;
        Long userId = 1L;
        String status = LikeStatus.LIKE.name();
        CommentLike commentLike = CommentLike.builder().commentId(commentId)
                .status(LikeStatus.NONE.name())
                .userId(userId).build();

        when(commentLikeRepository.findByCommentIdAndUserId(commentId, userId))
                .thenReturn(Optional.ofNullable(commentLike));
        ArgumentCaptor<CommentLike> captor = ArgumentCaptor.forClass(CommentLike.class);
        when(commentLikeRepository.update(captor.capture())).thenReturn(1);

        commentLikeService.changeLikeStatus(commentId, userId, status);
        CommentLike capturedCommentLike = captor.getValue();

        assertEquals(status, capturedCommentLike.getStatus());
    }

    @Test
    void testRemoveLikeOrDislike_success() {
        Long commentId = 1L;
        Long userId = 1L;
        CommentLike commentLike = CommentLike.builder().commentId(commentId)
                .userId(userId).build();

        when(commentLikeRepository.findByCommentIdAndUserId(commentId, userId))
                .thenReturn(Optional.ofNullable(commentLike));
        ArgumentCaptor<CommentLike> captor = ArgumentCaptor.forClass(CommentLike.class);
        doNothing().when(commentLikeRepository).delete(captor.capture());

        commentLikeService.removeLikeOrDislike(commentId, userId);
        CommentLike capturedCommentLike = captor.getValue();

        assertEquals(commentId, capturedCommentLike.getCommentId());
        assertEquals(userId, capturedCommentLike.getUserId());
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