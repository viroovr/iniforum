package com.forum.project.presentation.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.project.domain.report.service.QuestionReportService;
import com.forum.project.domain.auth.service.AuthorizationService;
import com.forum.project.domain.question.controller.QuestionReportController;
import com.forum.project.domain.report.dto.ReportRequestDto;
import com.forum.project.domain.report.entity.QuestionReport;
import com.forum.project.presentation.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(QuestionReportController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class QuestionReportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionReportService questionReportService;
    @MockBean
    private AuthorizationService authorizationService;

    @Test
    void testSaveReportQuestion_success() throws Exception {
        Long questionId = 1L;
        Long userId = 1L;
        String header = "Bearer accessToken";
        ReportRequestDto reportRequestDto = new ReportRequestDto("스팸");

        when(authorizationService.extractUserId(header)).thenReturn(userId);
        doNothing().when(questionReportService).saveReport(questionId, userId, reportRequestDto.getReason());

        mockMvc.perform(post("/api/v1/questions/report/{id}", questionId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", header)
                    .content(new ObjectMapper().writeValueAsString(reportRequestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Question Reported Successfully."));
    }

    @Test
    void testGetQuestionReport_success() throws Exception {
        Long questionId = 1L;
        String header = "Bearer accessToken";

        List<QuestionReport> reportList = List.of(
                QuestionReport.builder().questionId(questionId).userId(1L).build(),
                QuestionReport.builder().questionId(questionId).userId(2L).build()
        );
        doNothing().when(authorizationService).validateAdminRole(header);
        when(questionReportService.getReportsById(questionId)).thenReturn(reportList);

        mockMvc.perform(get("/api/v1/questions/report/{id}", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", header))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[1].userId").value(2L));
    }

    @Test
    void testGetQuestionReportByUser_success() throws Exception {
        Long userId = 1L;
        String header = "Bearer accessToken";

        List<QuestionReport> reportList = List.of(
                QuestionReport.builder().questionId(1L).userId(userId).build(),
                QuestionReport.builder().questionId(2L).userId(userId).build()
        );
        doNothing().when(authorizationService).validateUser(userId, header);
        when(questionReportService.getReportsByUserId(userId)).thenReturn(reportList);

        mockMvc.perform(get("/api/v1/questions/report/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", header))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].questionId").value(1L))
                .andExpect(jsonPath("$[1].questionId").value(2L));
    }

    @Test
    void testResolveQuestionReport_success() throws Exception {
        Long reportId = 1L;
        String header = "Bearer accessToken";

        doNothing().when(authorizationService).validateAdminRole(header);
        doNothing().when(questionReportService).resolveReport(reportId);

        mockMvc.perform(post("/api/v1/questions/report/resolve/{id}", reportId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", header))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Question Resolved Successfully."));
    }
}
