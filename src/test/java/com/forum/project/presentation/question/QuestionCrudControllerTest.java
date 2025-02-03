package com.forum.project.presentation.question;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.project.application.question.QuestionCrudService;
import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.domain.user.User;
import com.forum.project.presentation.config.TestSecurityConfig;
import com.forum.project.presentation.dtos.BaseResponseDto;
import com.forum.project.presentation.question.dto.QuestionCreateDto;
import com.forum.project.presentation.question.dto.QuestionRequestDto;
import com.forum.project.presentation.question.dto.QuestionResponseDto;
import com.forum.project.presentation.question.dto.QuestionUpdateDto;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(QuestionCrudController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class QuestionCrudControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionCrudService questionCrudService;
    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void testPostQuestion_success() throws Exception {
        QuestionRequestDto questionRequestDto = QuestionRequestDto.builder()
                .title("testTitle")
                .content("testContent").build();
        String header = "Bearer accessToken";
        User user = User.builder().id(1L).build();
        QuestionResponseDto questionResponseDto = QuestionResponseDto.builder()
                .title("testTitle")
                .content("testContent").build();

        when(authenticationService.extractUserByHeader(header)).thenReturn(user);
        when(questionCrudService.create(any(QuestionCreateDto.class))).thenReturn(questionResponseDto);

        mockMvc.perform(post("/api/v1/questions/post")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", header)
                    .content(new ObjectMapper().writeValueAsString(questionRequestDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.title").value("testTitle"))
                .andExpect(jsonPath("$.content").value("testContent"));
    }

    @Test
    void testReadQuestion_success() throws Exception {
        Long questionId = 1L;
        String header = "Bearer accessToken";
        Long userId = 1L;
        QuestionResponseDto questionResponseDto = QuestionResponseDto.builder()
                .title("testTitle")
                .content("testContent").build();

        when(authenticationService.extractUserId(header)).thenReturn(userId);
        when(questionCrudService.readQuestion(questionId, userId)).thenReturn(questionResponseDto);

        mockMvc.perform(get("/api/v1/questions/{id}", questionId)
                    .header("Authorization", header))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value("testTitle"))
                .andExpect(jsonPath("$.content").value("testContent"));
    }

    @Test
    void testUpdateQuestion_success() throws Exception {
        Long questionId = 1L;
        QuestionRequestDto questionRequestDto = QuestionRequestDto.builder()
                .title("testTitle")
                .content("testContent").build();
        String header = "Bearer accessToken";
        Long userId = 1L;
        QuestionResponseDto questionResponseDto = QuestionResponseDto.builder()
                .title("testTitle")
                .content("testContent").build();

        when(authenticationService.extractUserId(header)).thenReturn(userId);
        when(questionCrudService.updateTitleAndContent(questionId, userId, "testTitle", "testContent"))
                .thenReturn(questionResponseDto);

        mockMvc.perform(put("/api/v1/questions/{id}", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", header)
                        .content(new ObjectMapper().writeValueAsString(questionRequestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value("testTitle"))
                .andExpect(jsonPath("$.content").value("testContent"));
    }

    @Test
    void testDeleteQuestion_success() throws Exception {
        Long questionId = 1L;
        String header = "Bearer accessToken";
        Long userId = 1L;
        BaseResponseDto baseResponseDto = BaseResponseDto.builder()
                .message("Question deleted successfully.").build();

        when(authenticationService.extractUserId(header)).thenReturn(userId);
        doNothing().when(questionCrudService).delete(questionId, userId);

        mockMvc.perform(delete("/api/v1/questions/{id}", questionId)
                        .header("Authorization", header))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.message").value(baseResponseDto.getMessage()));
    }
}
