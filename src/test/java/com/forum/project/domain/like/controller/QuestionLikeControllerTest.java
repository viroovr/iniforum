package com.forum.project.domain.like.controller;

import com.forum.project.domain.like.service.QuestionLikeService;
import com.forum.project.domain.question.vo.QuestionContext;
import com.forum.project.testUtils.CustomMockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(CustomMockMvc.class)
@WebMvcTest(controllers = QuestionLikeController.class)
public class QuestionLikeControllerTest {
    @Autowired
    private CustomMockMvc customMockMvc;

    @MockBean private QuestionLikeService questionLikeService;
    private static final String BASE_PATH = "/api/v1/questions";

    @Test
    void likeQuestion() throws Exception {
        customMockMvc.perform(put(BASE_PATH + "/like/{questionId}", 1)
                        .header("X-Forwarded-For", "192.168.0.1")
                        .header("Authorization", CustomMockMvc.AUTHORIZATION_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        verify(questionLikeService).likeQuestion(any(QuestionContext.class), eq("192.168.0.1"));
    }

    @Test
    void deleteQuestionLike() throws Exception {
        customMockMvc.deleteRequestWithoutDto(BASE_PATH + "/like/{questionId}", 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }
}
