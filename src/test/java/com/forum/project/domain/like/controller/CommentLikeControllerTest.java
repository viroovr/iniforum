package com.forum.project.domain.like.controller;

import com.forum.project.domain.comment.service.CommentService;
import com.forum.project.testUtils.CustomMockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(CustomMockMvc.class)
@WebMvcTest(CommentLikeController.class)
class CommentLikeControllerTest {
    @Autowired
    private CustomMockMvc customMockMvc;

    @MockBean private CommentService commentService;

    private static final String BASE_PATH = "/api/v1/comments";

    @Test
    void likeComment() throws Exception {
        customMockMvc.putRequestWithoutDto(BASE_PATH + "/like/{questionId}/{commentId}", 1, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        verify(commentService).likeComment(argThat(argument -> argument.userId().equals(1L)));
    }

    @Test
    void dislikeComment() throws Exception {
        customMockMvc.putRequestWithoutDto(BASE_PATH + "/dislike/{questionId}/{commentId}", 1, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        verify(commentService).dislikeComment(argThat(argument -> argument.userId().equals(1L)));
    }
}