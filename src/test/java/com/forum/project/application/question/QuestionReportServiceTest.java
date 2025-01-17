package com.forum.project.application.question;

import com.forum.project.application.email.EmailAdminService;
import com.forum.project.domain.report.comment.CommentReport;
import com.forum.project.domain.report.question.QuestionReport;
import com.forum.project.domain.report.question.QuestionReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        doNothing().when(questionValidator).existsQuestion(questionId);
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
}
