package com.forum.project.application.comment;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.user.service.UserProfileService;
import com.forum.project.domain.auth.service.AuthorizationService;
import com.forum.project.domain.comment.entity.Comment;
import com.forum.project.domain.comment.service.CommentService;
import com.forum.project.domain.like.service.CommentLikeService;
import com.forum.project.domain.report.service.CommentReportService;
import com.forum.project.infrastructure.persistence.key.CommentKey;
import com.forum.project.domain.comment.repository.CommentRepository;
import com.forum.project.domain.comment.dto.CommentRequestDto;
import com.forum.project.domain.comment.dto.CommentResponseDto;
import com.forum.project.domain.report.dto.ReportRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private AuthorizationService authorizationService;

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private CommentService commentService;

    private Map<String, Object> generateKeys(Long id, Timestamp timestamp) {
        Map<String, Object> generatedKeys = new HashMap<>();
        generatedKeys.put(CommentKey.ID, id);
        generatedKeys.put(CommentKey.CREATED_DATE, timestamp);
        generatedKeys.put(CommentKey.LAST_MODIFIED_DATE, timestamp);
        return generatedKeys;
    }

    @Test
    void testAddComment_Success() {
        CommentRequestDto commentRequestDto = new CommentRequestDto("requestContent");
        Long userId = 1L;
        Long questionId = 1L;
        String loginId = "testUser";
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        Map<String, Object> generatedKeys = generateKeys(1L, timestamp);

        when(commentRepository.insertAndReturnGeneratedKeys(any(Comment.class))).thenReturn(generatedKeys);
        when(userProfileService.getLoginId(userId)).thenReturn(loginId);

        CommentResponseDto result = commentService.addComment(questionId, userId, commentRequestDto);

        assertNotNull(result);
        assertEquals("requestContent", result.getContent());
        assertEquals(loginId, result.getLoginId());
        assertEquals(timestamp.toLocalDateTime(), result.getCreatedDate());
        assertEquals(0L, result.getLikeCount());
    }

    @Test
    void testGetCommentsByQuestionId_Success() {
        Long questionId = 1L;
        List<Comment> response = List.of(
                Comment.builder().id(1L).questionId(questionId).build(),
                Comment.builder().id(2L).questionId(questionId).build()
        );
        when(commentRepository.findAllByQuestionId(questionId)).thenReturn(response);

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
        when(authorizationService.extractUserId(header)).thenReturn(userId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).deleteById(commentId);

        commentService.deleteComment(commentId, header);

        verify(authorizationService).extractUserId(header);
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
        when(authorizationService.extractUserId(header)).thenReturn(userId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).deleteById(commentId);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> commentService.deleteComment(commentId, header));

        assertEquals(ErrorCode.AUTH_BAD_CREDENTIAL, applicationException.getErrorCode());
        verify(authorizationService).extractUserId(header);
        verify(commentRepository).findById(commentId);
        verify(commentRepository, never()).deleteById(commentId);
    }

    @Test
    void testUpdateComment_Success() {
        Long commentId = 1L;
        Long userId = 1L;
        CommentRequestDto commentRequestDto = new CommentRequestDto("newContent");
        Comment comment = Comment.builder()
                .id(commentId)
                .content("originalContent")
                .userId(userId).build();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        doNothing().when(commentRepository).updateContent(commentId, commentRequestDto.getContent());

        commentService.updateComment(commentId, userId, commentRequestDto);

        assertEquals(comment.getContent(), commentRequestDto.getContent());
    }

    @Test
    void testLikeComment_Success() {
        Long commentId = 1L;
        Long userId = 1L;
        Optional<Comment> optionalComment = Optional.of(Comment.builder().downVotedCount(0L).build());

        doNothing().when(commentLikeService).addLikeComment(commentId, userId);
        when(commentRepository.findById(commentId)).thenReturn(optionalComment);

        doNothing().when(commentRepository).updateDownVotedCount(commentId, 1L);
        commentService.likeComment(commentId, userId);

        assertEquals(1L, optionalComment.get().getUpVotedCount());
    }

    @Test
    void testDislikeComment_Success() {
        Long commentId = 1L;
        Long userId = 1L;
        Optional<Comment> optionalComment = Optional.of(
                Comment.builder()
                    .downVotedCount(0L)
                    .build());

        doNothing().when(commentLikeService).addDislikeComment(commentId, userId);
        when(commentRepository.findById(commentId)).thenReturn(optionalComment);

        doNothing().when(commentRepository).updateDownVotedCount(commentId, 1L);
        commentService.dislikeComment(commentId, userId);

        assertEquals(1L, optionalComment.get().getDownVotedCount());
    }

    @Test
    void shouldThrowCommentNotFound_whenCommentIsEmpty() {
        Long commentId = 1L;
        Long userId = 1L;

        doNothing().when(commentLikeService).addDislikeComment(commentId, userId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> commentService.dislikeComment(commentId, userId));

        assertEquals(ErrorCode.COMMENT_NOT_FOUND, applicationException.getErrorCode());
    }

    @Test
    void testReportComment_Success() {
        Long commentId = 1L;
        Long userId = 1L;
        ReportRequestDto dto = new ReportRequestDto("validReason");
        Optional<Comment> comment = Optional.of(Comment.builder().reportCount(0L).build());

        doNothing().when(commentReportService).saveReport(commentId, userId, dto.getReason());
        doNothing().when(commentReportService).notifyAdminIfHighReports(commentId);
        when(commentRepository.findById(commentId)).thenReturn(comment);

        doNothing().when(commentRepository).updateContent(commentId, "newContent");
        commentService.reportComment(commentId, userId, dto);

        assertEquals(1L, comment.get().getReportCount());
    }

    @Test
    void testGetUserComments_Success() {
        String header = "validHeader";
        Long userId = 1L;
        List<Comment> commentList = List.of(
            Comment.builder().id(1L).userId(userId).loginId("loginId1").build(),
            Comment.builder().id(2L).userId(userId).loginId("loginId2").build()
        );

        when(commentRepository.findAllByUserId(userId)).thenReturn(commentList);

        List<CommentResponseDto> response = commentService.getUserComments(userId, header);

        assertNotNull(response);
        assertEquals("loginId1", response.get(0).getLoginId());
        assertEquals("loginId2", response.get(1).getLoginId());
    }

    @Test
    void testReplyToComment_Success() {
        Long parentCommentId = 1L;
        Long userId = 1L;
        String loginId = "validLoginId";
        CommentRequestDto dto = new CommentRequestDto("validContent");
        Comment parentComment = Comment.builder()
                .questionId(1L)
                .parentCommentId(parentCommentId).build();
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        Map<String, Object> generatedKeys = generateKeys(1L, timestamp);

        when(commentRepository.findById(parentCommentId)).thenReturn(Optional.of(parentComment));
        when(userProfileService.getLoginId(userId)).thenReturn(loginId);
        when(commentRepository.insertAndReturnGeneratedKeys(any(Comment.class))).thenReturn(generatedKeys);

        CommentResponseDto result = commentService.replyToComment(parentCommentId, userId, dto);

        assertNotNull(result);
        assertEquals("validContent", result.getContent());
        assertEquals(loginId, result.getLoginId());
        assertEquals(timestamp.toLocalDateTime(), result.getCreatedDate());
        assertEquals(0L, result.getLikeCount());
    }

    @Test
    void testGetChildComments_Success() {
        Long parentCommentId = 1L;
        List<Comment> commentList = List.of(
                Comment.builder().id(1L).parentCommentId(parentCommentId).loginId("loginId1").build(),
                Comment.builder().id(2L).parentCommentId(parentCommentId).loginId("loginId2").build()
        );

        when(commentRepository.findAllByParentCommentId(parentCommentId)).thenReturn(commentList);

        List<CommentResponseDto> response = commentService.getChildComments(parentCommentId);

        assertNotNull(response);
        assertEquals("loginId1", response.get(0).getLoginId());
        assertEquals("loginId2", response.get(1).getLoginId());
    }
}