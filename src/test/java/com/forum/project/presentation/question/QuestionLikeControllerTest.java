package com.forum.project.presentation.question;

import com.forum.project.domain.like.service.QuestionLikeService;
import com.forum.project.domain.auth.service.AuthorizationService;
import com.forum.project.domain.like.vo.LikeStatus;
import com.forum.project.domain.question.controller.QuestionLikeController;
import com.forum.project.presentation.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = QuestionLikeController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class QuestionLikeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private QuestionLikeService questionLikeService;
    @MockBean
    private AuthorizationService authorizationService;

    @Test
    void testAddQuestionLike_success() throws Exception {
        Long questionId = 1L;
        Long userId = 1L;
        LikeStatus likeStatus = LikeStatus.LIKE;
        String header = "Bearer accessToken";
        String ipAddress = "192.168.0.1";

        when(authorizationService.extractUserId(header)).thenReturn(userId);
        doNothing().when(questionLikeService).addLike(questionId, userId, likeStatus, ipAddress);

        mockMvc.perform(put("/api/v1/questions/like/{id}", questionId)
                        .param("status", likeStatus.name())
                        .header("Authorization", header)
                        .header("X-Forwarded-For", ipAddress))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.message").value(String.format("%s Question successfully.", likeStatus)));
    }

    @Test
    void testDeleteQuestionLike_success() throws Exception {
        Long questionId = 1L;
        Long userId = 1L;
        LikeStatus likeStatus = LikeStatus.LIKE;
        String header = "Bearer accessToken";

        when(authorizationService.extractUserId(header)).thenReturn(userId);
        doNothing().when(questionLikeService).cancelLike(questionId, userId, likeStatus);

        mockMvc.perform(delete("/api/v1/questions/like/{id}", questionId)
                        .param("status", likeStatus.name())
                        .header("Authorization", header))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.message").value(
                        String.format("Delete %s Question successfully.", likeStatus)));
    }
}
