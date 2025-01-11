package com.forum.project.application.comment;

import com.forum.project.application.email.EmailAdminService;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.comment.CommentReport;
import com.forum.project.domain.comment.CommentReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentReportServiceTest {
    @Mock
    private CommentReportRepository commentReportRepository;

    @Mock
    private EmailAdminService emailAdminService;

    @InjectMocks
    private CommentReportService commentReportService;

    @Test
    void testSaveReportComment_success() {
        Long commentId = 1L;
        Long userId = 1L;
        String reason = "스팸";

        ArgumentCaptor<CommentReport> argumentCaptor = ArgumentCaptor.forClass(CommentReport.class);
        when(commentReportRepository.save(argumentCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(commentReportRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(false);

        commentReportService.saveReportComment(commentId, userId, reason);

        CommentReport commentReport = argumentCaptor.getValue();

        assertEquals(commentId, commentReport.getCommentId());
        assertEquals(userId, commentReport.getUserId());
        assertEquals(reason, commentReport.getReason());
    }

    @Test
    void testSaveReportComment_throwCommentAlreadyReported() {
        Long commentId = 1L;
        Long userId = 1L;
        String reason = "스팸";

        when(commentReportRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(true);

        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> commentReportService.saveReportComment(commentId, userId, reason));

        assertEquals(exception.getErrorCode(), ErrorCode.COMMENT_ALREADY_REPORTED);
    }

    @Test
    void throwInvalidCommentReport_whenReasonIsEmpty() {
        Long commentId = 1L;
        Long userId = 1L;
        String reason = "";

        when(commentReportRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(false);

        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> commentReportService.saveReportComment(commentId, userId, reason));

        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COMMENT_REPORT);
    }

    @Test
    void throwInvalidCommentReport_whenReasonNotInRange() {
        Long commentId = 1L;
        Long userId = 1L;
        String reason = "없는 신고 사유";

        when(commentReportRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(false);

        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> commentReportService.saveReportComment(commentId, userId, reason));

        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COMMENT_REPORT);
    }

    @Test
    void testNotifyAdminIfHighReports_success() {
        Long commentId = 1L;
        Long reportCount = 11L;

        when(commentReportRepository.countByCommentId(commentId)).thenReturn(reportCount);
        doNothing().when(emailAdminService).sendEmail(anyString(), anyString());

        commentReportService.notifyAdminIfHighReports(commentId);

        verify(commentReportRepository).countByCommentId(commentId);
        verify(emailAdminService).sendEmail(anyString(), anyString());
    }

    @Test
    void testNotifyAdminIfHighReports_notReported() {
        Long commentId = 1L;
        Long reportCount = 9L;

        when(commentReportRepository.countByCommentId(commentId)).thenReturn(reportCount);

        commentReportService.notifyAdminIfHighReports(commentId);

        verify(commentReportRepository).countByCommentId(commentId);
        verify(emailAdminService, never()).sendEmail(anyString(), anyString());
    }

    @Test
    void testGetReportsByCommentId_success() {
        Long commentId = 1L;
        List<CommentReport> list = List.of(
                CommentReport.builder().id(1L).commentId(commentId).build(),
                CommentReport.builder().id(2L).commentId(commentId).build()
        );

        when(commentReportRepository.findAllByCommentId(commentId)).thenReturn(list);

        List<CommentReport> response = commentReportService.getReportsByCommentId(commentId);

        assertNotNull(response);
        assertEquals(1L, response.get(0).getId());
        assertEquals(2L, response.get(1).getId());
        assertEquals(commentId, response.get(1).getCommentId());
    }

    @Test
    void testGetReportsByUserId_success() {
        Long userId = 1L;
        List<CommentReport> list = List.of(
                CommentReport.builder().id(1L).userId(userId).build(),
                CommentReport.builder().id(2L).userId(userId).build()
        );

        when(commentReportRepository.findAllByUserId(userId)).thenReturn(list);

        List<CommentReport> response = commentReportService.getReportsByUserId(userId);

        assertNotNull(response);
        assertEquals(1L, response.get(0).getId());
        assertEquals(2L, response.get(1).getId());
        assertEquals(userId, response.get(0).getUserId());
    }

    @Test
    void testResolveReport_success() {
        Long reportId = 1L;
        CommentReport commentReport = CommentReport.builder()
                .id(reportId)
                .isResolved(false).build();

        ArgumentCaptor<CommentReport> argumentCaptor = ArgumentCaptor.forClass(CommentReport.class);
        when(commentReportRepository.save(argumentCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(commentReportRepository.findById(reportId)).thenReturn(Optional.of(commentReport));

        commentReportService.resolveReport(reportId);

        CommentReport captorValue = argumentCaptor.getValue();

        assertEquals(reportId, captorValue.getId());
        assertTrue(captorValue.isResolved());
    }
}