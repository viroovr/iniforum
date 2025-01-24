package com.forum.project.application.question;

import com.forum.project.application.email.EmailAdminService;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.question.report.QuestionReport;
import com.forum.project.domain.question.report.QuestionReportRepository;
import com.forum.project.domain.report.ReportStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionReportServiceTest {
    @InjectMocks
    private QuestionReportService questionReportService;

    @Mock
    private QuestionValidator questionValidator;
    @Mock
    private QuestionReportRepository questionReportRepository;
    @Mock
    private EmailAdminService emailAdminService;

    @Test
    void testSaveReport() {
        Long questionId = 1L;
        Long userId = 1L;
        String reason = "스팸";
        Long reportCount = 1L;

        ArgumentCaptor<QuestionReport> argumentCaptor = ArgumentCaptor.forClass(QuestionReport.class);
        doNothing().when(questionValidator).validateQuestion(questionId);
        when(questionReportRepository.existsByIdAndUserId(questionId, userId)).thenReturn(false);
        when(questionReportRepository.save(any(QuestionReport.class))).thenAnswer(inv -> inv.getArgument(0));

        when(questionReportRepository.countByQuestionId(questionId)).thenReturn(reportCount);

        questionReportService.saveReport(questionId, userId, reason);

        verify(questionReportRepository).save(argumentCaptor.capture());
        QuestionReport questionReport = argumentCaptor.getValue();
        assertNotNull(questionReport);
        assertEquals(questionId, questionReport.getQuestionId());
        assertEquals(reason, questionReport.getReason());
    }

    @Test
    void testSaveReportQuestion_throwQuestionAlreadyReported() {
        Long questionId = 1L;
        Long userId = 1L;
        String reason = "스팸";

        when(questionReportRepository.existsByIdAndUserId(questionId, userId)).thenReturn(true);

        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> questionReportService.saveReport(questionId, userId, reason));

        assertEquals(exception.getErrorCode(), ErrorCode.QUESTION_ALREADY_REPORTED);
    }

    @Test
    void throwInvalidQuestionReport_whenReasonIsEmpty() {
        Long questionId = 1L;
        Long userId = 1L;
        String reason = "";

        when(questionReportRepository.existsByIdAndUserId(questionId, userId)).thenReturn(false);

        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> questionReportService.saveReport(questionId, userId, reason));

        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_REPORT);
    }

    @Test
    void throwInvalidQuestionReport_whenReasonNotInRange() {
        Long questionId = 1L;
        Long userId = 1L;
        String reason = "없는 신고 사유";

        when(questionReportRepository.existsByIdAndUserId(questionId, userId)).thenReturn(false);

        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> questionReportService.saveReport(questionId, userId, reason));

        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_REPORT);
    }

    @Test
    void testNotifyAdminIfHighReports_success() {
        Long questionId = 1L;
        Long reportCount = 11L;

        when(questionReportRepository.countByQuestionId(questionId)).thenReturn(reportCount);
        doNothing().when(emailAdminService).sendEmail(anyString(), anyString());

        questionReportService.notifyAdminIfHighReports(questionId);

        verify(questionReportRepository).countByQuestionId(questionId);
        verify(emailAdminService).sendEmail(anyString(), anyString());
    }

    @Test
    void testNotifyAdminIfHighReports_notReported() {
        Long questionId = 1L;
        Long reportCount = 9L;

        when(questionReportRepository.countByQuestionId(questionId)).thenReturn(reportCount);

        questionReportService.notifyAdminIfHighReports(questionId);

        verify(questionReportRepository).countByQuestionId(questionId);
        verify(emailAdminService, never()).sendEmail(anyString(), anyString());
    }

    @Test
    void testGetReportsByQuestionId_success() {
        Long questionId = 1L;
        List<QuestionReport> list = List.of(
                QuestionReport.builder().id(1L).questionId(questionId).build(),
                QuestionReport.builder().id(2L).questionId(questionId).build()
        );

        when(questionReportRepository.findAllByQuestionId(questionId)).thenReturn(list);

        List<QuestionReport> response = questionReportService.getReportsById(questionId);

        assertNotNull(response);
        assertEquals(1L, response.get(0).getId());
        assertEquals(2L, response.get(1).getId());
        assertEquals(questionId, response.get(1).getQuestionId());
    }

    @Test
    void testGetReportsByUserId_success() {
        Long userId = 1L;
        List<QuestionReport> list = List.of(
                QuestionReport.builder().id(1L).userId(userId).build(),
                QuestionReport.builder().id(2L).userId(userId).build()
        );

        when(questionReportRepository.findAllByUserId(userId)).thenReturn(list);

        List<QuestionReport> response = questionReportService.getReportsByUserId(userId);

        assertNotNull(response);
        assertEquals(1L, response.get(0).getId());
        assertEquals(2L, response.get(1).getId());
        assertEquals(userId, response.get(0).getUserId());
    }

    @Test
    void testResolveReport_success() {
        Long reportId = 1L;
        QuestionReport commentReport = QuestionReport.builder()
                .id(reportId).build();

        ArgumentCaptor<QuestionReport> argumentCaptor = ArgumentCaptor.forClass(QuestionReport.class);
        when(questionReportRepository.save(any(QuestionReport.class))).thenAnswer(inv -> inv.getArgument(0));
        when(questionReportRepository.findById(reportId)).thenReturn(Optional.of(commentReport));

        questionReportService.resolveReport(reportId);

        verify(questionReportRepository).save(argumentCaptor.capture());
        QuestionReport captorValue = argumentCaptor.getValue();

        assertEquals(reportId, captorValue.getId());
        assertEquals(ReportStatus.RESOLVED.name(), captorValue.getStatus());
    }
}
