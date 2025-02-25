package com.forum.project.domain.comment.service;

import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.comment.dto.CommentCreateDto;
import com.forum.project.domain.comment.dto.CommentRequestDto;
import com.forum.project.domain.comment.dto.CommentResponseDto;
import com.forum.project.domain.comment.entity.Comment;
import com.forum.project.domain.comment.repository.CommentRepository;
import com.forum.project.domain.comment.vo.CommentContext;
import com.forum.project.domain.like.service.CommentLikeService;
import com.forum.project.domain.report.dto.ReportRequestDto;
import com.forum.project.domain.report.service.CommentReportService;
import com.forum.project.domain.user.service.UserService;
import com.forum.project.infrastructure.persistence.key.CommentKey;
import com.forum.project.presentation.dtos.TestDtoFactory;
import com.forum.project.testUtils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.forum.project.domain.comment.dtofactory.CommentTestDtoFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock private UserService userService;
    @Mock private CommentRepository commentRepository;
    @Mock private CommentLikeService commentLikeService;
    @Mock private CommentReportService commentReportService;

    @InjectMocks
    private CommentService commentService;

    private CommentRequestDto commentRequestDto;
    private CommentCreateDto commentCreateDto;
    private CommentContext commentContext;
    private CommentKey commentKey;
    private Comment comment;

    @BeforeEach
    void setUp() {
        commentRequestDto = createCommentRequestDto();
        commentContext = createCommentContext();
        commentCreateDto = createCommentCreateDto();
        commentKey = createCommentKey();
        comment = createComment();
    }

    private void testAddComment() {
        when(commentRepository.insertAndReturnGeneratedKeys(commentCreateDto)).thenReturn(Optional.of(commentKey));
        when(userService.getLoginId(commentCreateDto.getUserId())).thenReturn(anyString());

        CommentResponseDto result = commentService.addComment(commentCreateDto);

        assertThat(result.getContent()).isEqualTo(commentCreateDto.getContent());
    }

    @Test
    void addComment() {
        testAddComment();
    }

    @Test
    void addComment_commentKeyGeneratedError() {
        when(commentRepository.insertAndReturnGeneratedKeys(commentCreateDto)).thenReturn(Optional.empty());

        TestUtils.assertApplicationException(
                () -> commentService.addComment(commentCreateDto),
                ErrorCode.DATABASE_ERROR);
    }

    @Test
    void addComment_reply() {
        commentCreateDto.setParentCommentId(1L);
        when(commentRepository.existsById(anyLong())).thenReturn(true);
        testAddComment();
    }

    @Test
    void addComment_reply_notValidatedId() {
        commentCreateDto.setParentCommentId(1L);
        when(commentRepository.existsById(anyLong())).thenReturn(false);

        TestUtils.assertApplicationException(
                () -> commentService.addComment(commentCreateDto),
                ErrorCode.COMMENT_NOT_FOUND);
    }

    @Nested
    public class GetCommentsTests {
        @BeforeEach
        void setUp() {
            when(userService.getLoginId(comment.getUserId())).thenReturn("loginId");
        }

        private void assertResult(List<CommentResponseDto> result) {
            assertThat(result)
                    .hasSize(1)
                    .extracting(CommentResponseDto::getContent, CommentResponseDto::getLoginId)
                    .containsExactly(tuple(comment.getContent(), "loginId"));
        }

        @Test
        void getCommentsByQuestionId() {
            when(commentRepository.findAllByQuestionId(1L)).thenReturn(List.of(comment));

            assertResult(commentService.getCommentsByQuestionId(1L));
        }

        @Test
        void getUserComments() {
            when(commentRepository.findAllByUserId(1L)).thenReturn(List.of(comment));

            assertResult(commentService.getUserComments(1L));
        }

        @Test
        void getChildComments() {
            when(commentRepository.findAllByParentCommentId(1L)).thenReturn(List.of(comment));

            assertResult(commentService.getChildComments(1L));
        }
    }

    @Nested
    public class CommentModificationTests {
        @BeforeEach
        void setUp() {
            when(commentRepository.findById(commentContext.commentId())).thenReturn(Optional.of(comment));
        }

        @Test
        void updateComment() {
            when(commentRepository.updateContent(commentContext.commentId(), commentRequestDto.getContent())).thenReturn(1);

            assertThatCode(() -> commentService.updateComment(commentRequestDto, commentContext)).doesNotThrowAnyException();
        }

        @Test
        void updateComment_failUpdating() {
            when(commentRepository.updateContent(commentContext.commentId(), commentRequestDto.getContent())).thenReturn(0);
            TestUtils.assertApplicationException(
                    () -> commentService.updateComment(commentRequestDto, commentContext),
                    ErrorCode.DATABASE_ERROR
            );
        }

        @Test
        void updateComment_notFoundComment() {
            when(commentRepository.findById(commentContext.commentId())).thenReturn(Optional.empty());
            TestUtils.assertApplicationException(
                    () -> commentService.updateComment(commentRequestDto, commentContext),
                    ErrorCode.COMMENT_NOT_FOUND
            );
        }

        private void testValidatingCommentContext_whenUpdateComment() {
            TestUtils.assertApplicationException(
                    () -> commentService.updateComment(commentRequestDto, commentContext),
                    ErrorCode.AUTH_BAD_CREDENTIAL
            );
        }

        @Test
        void updateComment_notOwner() {
            comment.setUserId(123L);
            testValidatingCommentContext_whenUpdateComment();
        }

        @Test
        void updateComment_notIncludedQuestionId() {
            comment.setQuestionId(123L);
            testValidatingCommentContext_whenUpdateComment();
        }

        @Test
        void deleteComment() {
            when(commentRepository.deleteById(commentContext.commentId())).thenReturn(1);

            assertThatCode(() -> commentService.deleteComment(commentContext)).doesNotThrowAnyException();
        }
    }

    @Nested
    public class CommentInteractionTests {
        @BeforeEach
        void setUp() {
            when(commentRepository.findById(commentContext.commentId())).thenReturn(Optional.of(comment));
        }

        @Test
        void likeComment() {
            when(commentRepository.updateUpVotedCount(commentContext.commentId(), 1L)).thenReturn(1);
            assertThatCode(() -> commentService.likeComment(commentContext)).doesNotThrowAnyException();

            verify(commentLikeService).addLikeComment(commentContext.commentId(), commentContext.userId());
        }

        @Test
        void likeComment_failUpdating() {
            when(commentRepository.updateUpVotedCount(commentContext.commentId(), 1L)).thenReturn(0);
            TestUtils.assertApplicationException(
                    () -> commentService.likeComment(commentContext),
                    ErrorCode.DATABASE_ERROR
            );
        }

        @Test
        void dislikeComment() {
            when(commentRepository.updateDownVotedCount(commentContext.commentId(), 1L)).thenReturn(1);
            assertThatCode(() -> commentService.dislikeComment(commentContext)).doesNotThrowAnyException();

            verify(commentLikeService).addDislikeComment(commentContext.commentId(), commentContext.userId());
        }

        @Test
        void reportComment() {
            ReportRequestDto reportRequestDto = TestDtoFactory.createReportRequestDto();
            when(commentRepository.updateReportCount(commentContext.commentId(), 1L)).thenReturn(1);
            assertThatCode(() -> commentService.reportComment(reportRequestDto, commentContext))
                    .doesNotThrowAnyException();

            verify(commentReportService).saveReport(commentContext, reportRequestDto);
            verify(commentReportService).notifyAdminIfHighReports(commentContext.commentId());
        }
    }
}