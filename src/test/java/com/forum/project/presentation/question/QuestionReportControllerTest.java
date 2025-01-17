package com.forum.project.presentation.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.project.application.question.QuestionReportService;
import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.domain.report.ReportRequestDto;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
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
    private AuthenticationService authenticationService;

    @Test
    void testReportQuestion() throws Exception {
        Long questionId = 1L;
        Long userId = 1L;
        String header = "Bearer accessToken";
        ReportRequestDto reportRequestDto = new ReportRequestDto("스팸");

        when(authenticationService.extractUserId(header)).thenReturn(userId);
        doNothing().when(questionReportService).saveReport(questionId, userId, reportRequestDto.getReason());

        mockMvc.perform(post("/api/v1/questions/report/{id}", questionId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", header)
                    .content(new ObjectMapper().writeValueAsString(reportRequestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Question Reported Successfully."));
    }
}
