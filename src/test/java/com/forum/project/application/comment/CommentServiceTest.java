package com.forum.project.application.comment;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.domain.comment.Comment;
import com.forum.project.domain.commentlike.CommentReportRequestDto;
import com.forum.project.infrastructure.persistence.comment.CommentRepository;
import com.forum.project.presentation.comment.CommentRequestDto;
import com.forum.project.presentation.comment.CommentResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeService commentLikeService;

    @Mock
    private CommentReportService commentReportService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private CommentService commentService;

    @Test
    void testAddComment_Success() {
        CommentRequestDto commentRequestDto = new CommentRequestDto("requestContent");
        String header = "validHeader";
        Long userId = 1L;
        Long questionId = 1L;
        String loginId = "testUser";

        when(authenticationService.extractUserId(header)).thenReturn(userId);
        when(authenticationService.extractLoginId(header)).thenReturn(loginId);
        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);
        when(commentRepository.save(commentArgumentCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CommentResponseDto result = commentService.addComment(questionId, commentRequestDto, header);

        Comment capturedComment = commentArgumentCaptor.getValue();

        assertNotNull(result);
        assertEquals("requestContent", capturedComment.getContent());
        assertEquals(questionId, capturedComment.getQuestionId());
        assertEquals(loginId, capturedComment.getLoginId());
        assertEquals(userId, capturedComment.getUserId());
        assertEquals(0L, capturedComment.getUpVotedCount());
        assertEquals(0L, capturedComment.getDownVotedCount());
        assertEquals(0L, capturedComment.getReportCount());
        assertFalse(capturedComment.getIsEdited());

        assertEquals(capturedComment.getContent(), result.getContent());
    }

    @Test
    void testGetCommentsByQuestionId_Success() {
        Long questionId = 1L;
        List<Comment> response = List.of(
                Comment.builder().id(1L).questionId(questionId).build(),
                Comment.builder().id(2L).questionId(questionId).build()
        );
        when(commentRepository.findByQuestionId(questionId)).thenReturn(response);

        List<CommentResponseDto> result = commentService.getCommentsByQuestionId(questionId);

        assertNotNull(result);
        assertEquals(1L, response.get(0).getId());
        assertEquals(2L, response.get(1).getId());
        assertEquals(questionId, response.get(1).getQuestionId());
    }

    @Test
    void testDeleteComment_Success() {
        Long commentId = 1L;
        String header = "validHeader";
        Long userId = 1L;
        Comment comment = Comment.builder().userId(userId).build();
        when(authenticationService.extractUserId(header)).thenReturn(userId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).deleteById(commentId);

        commentService.deleteComment(commentId, header);

        verify(authenticationService).extractUserId(header);
        verify(commentRepository).findById(commentId);
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void shouldThrowAuthBadCredential_whenLoginIdNotEqual() {
        Long commentId = 1L;
        String header = "validHeader";
        Long userId = 123L;
        Comment comment = Comment.builder()
                .userId(1500L).build();
        when(authenticationService.extractUserId(header)).thenReturn(userId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).deleteById(commentId);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> commentService.deleteComment(commentId, header));

        assertEquals(ErrorCode.AUTH_BAD_CREDENTIAL, applicationException.getErrorCode());
        verify(authenticationService).extractUserId(header);
        verify(commentRepository).findById(commentId);
        verify(commentRepository, never()).deleteById(commentId);
    }

    @Test
    void testUpdateComment_Success() {
        Long commentId = 1L;
        CommentRequestDto commentRequestDto = new CommentRequestDto("requestContent");
        String header = "validHeader";
        Long userId = 1L;
        Comment comment = Comment.builder()
                .id(commentId)
                .userId(userId).build();
        when(authenticationService.extractUserId(header)).thenReturn(userId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        ArgumentCaptor<Comment> argumentCaptor = ArgumentCaptor.forClass(Comment.class);
        when(commentRepository.update(argumentCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CommentResponseDto result = commentService.updateComment(commentId, commentRequestDto, header);

        Comment capturedComment = argumentCaptor.getValue();

        assertNotNull(result);
        assertEquals("requestContent", capturedComment.getContent());
        assertEquals(commentId, capturedComment.getId());
        assertEquals(userId, capturedComment.getUserId());
        assertTrue(capturedComment.getIsEdited());
    }

    @Test
    void testLikeComment_Success() {
        String header = "validHeader";
        Long commentId = 1L;
        Long userId = 1L;
        Comment comment = Comment.builder().upVotedCount(0L).build();
        when(authenticationService.extractUserId(header)).thenReturn(userId);

        doNothing().when(commentLikeService).addLikeComment(commentId, userId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        ArgumentCaptor<Comment> argumentCaptor = ArgumentCaptor.forClass(Comment.class);
        when(commentRepository.save(argumentCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        commentService.likeComment(commentId, header);

        Comment captorValue = argumentCaptor.getValue();
        assertEquals(1L, captorValue.getUpVotedCount());
    }

    @Test
    void testDislikeComment_Success() {
        String header = "validHeader";
        Long commentId = 1L;
        Long userId = 1L;
        Comment comment = Comment.builder().downVotedCount(0L).build();
        when(authenticationService.extractUserId(header)).thenReturn(userId);

        doNothing().when(commentLikeService).addDislikeComment(commentId, userId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        ArgumentCaptor<Comment> argumentCaptor = ArgumentCaptor.forClass(Comment.class);
        when(commentRepository.save(argumentCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        commentService.dislikeComment(commentId, header);

        Comment captorValue = argumentCaptor.getValue();
        assertEquals(1L, captorValue.getDownVotedCount());
    }

    @Test
    void shouldThrowCommentNotFound_whenCommentIsEmpty() {
        String header = "validHeader";
        Long commentId = 1L;
        Long userId = 1L;
        when(authenticationService.extractUserId(header)).thenReturn(userId);

        doNothing().when(commentLikeService).addDislikeComment(commentId, userId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> commentService.dislikeComment(commentId, header));

        assertEquals(ErrorCode.COMMENT_NOT_FOUND, applicationException.getErrorCode());
    }

    @Test
    void testReportComment_Success() {
        Long commentId = 1L;
        String header = "validHeader";
        CommentReportRequestDto dto = new CommentReportRequestDto("validReason");
        Long userId = 1L;
        Comment comment = Comment.builder().reportCount(0L).build();
        when(authenticationService.extractUserId(header)).thenReturn(userId);

        doNothing().when(commentReportService).saveReportComment(commentId, userId, dto.getReason());
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        ArgumentCaptor<Comment> argumentCaptor = ArgumentCaptor.forClass(Comment.class);
        when(commentRepository.update(argumentCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        commentService.reportComment(commentId, header, dto);

        Comment captorValue = argumentCaptor.getValue();
        assertEquals(1L, captorValue.getReportCount());
    }

    @Test
    void testGetUserComments_Success() {
        String header = "validHeader";
        Long userId = 1L;
        List<Comment> commentList = List.of(
            Comment.builder().id(1L).userId(userId).loginId("loginId1").build(),
            Comment.builder().id(2L).userId(userId).loginId("loginId2").build()
        );

        when(commentRepository.findByUserId(userId)).thenReturn(commentList);

        List<CommentResponseDto> response = commentService.getUserComments(userId, header);

        assertNotNull(response);
        assertEquals("loginId1", response.get(0).getLoginId());
        assertEquals("loginId2", response.get(1).getLoginId());
    }

    @Test
    void testReplyToComment_Success() {
        Long parentCommentId = 1L;
        String header = "Bearer validHeader";
        Long userId = 1L;
        String loginId = "validLoginId";
        CommentRequestDto dto = new CommentRequestDto("validContent");
        Comment parentComment = Comment.builder()
                .questionId(1L)
                .parentCommentId(parentCommentId).build();
        when(authenticationService.extractUserId(header)).thenReturn(userId);
        when(authenticationService.extractLoginId(header)).thenReturn(loginId);
        when(commentRepository.findById(parentCommentId)).thenReturn(Optional.of(parentComment));
        ArgumentCaptor<Comment> argumentCaptor = ArgumentCaptor.forClass(Comment.class);
        when(commentRepository.save(argumentCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CommentResponseDto response = commentService.replyToComment(parentCommentId, dto, header);

        Comment captorValue = argumentCaptor.getValue();
        assertNotNull(response);
        assertEquals(parentCommentId, captorValue.getParentCommentId());
        assertEquals(userId, captorValue.getUserId());
        assertEquals(loginId, captorValue.getLoginId());
        assertEquals(1L, captorValue.getQuestionId());
        assertEquals(loginId, response.getLoginId());
    }

    @Test
    void testGetChildComments_Success() {
        Long parentCommentId = 1L;
        List<Comment> commentList = List.of(
                Comment.builder().id(1L).parentCommentId(parentCommentId).loginId("loginId1").build(),
                Comment.builder().id(2L).parentCommentId(parentCommentId).loginId("loginId2").build()
        );

        when(commentRepository.findByParentCommentId(parentCommentId)).thenReturn(commentList);

        List<CommentResponseDto> response = commentService.getChildComments(parentCommentId);

        assertNotNull(response);
        assertEquals("loginId1", response.get(0).getLoginId());
        assertEquals("loginId2", response.get(1).getLoginId());
    }
}