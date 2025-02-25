package com.forum.project.domain.comment.controller;

import com.forum.project.domain.comment.dto.CommentCreateDto;
import com.forum.project.domain.comment.dto.CommentRequestDto;
import com.forum.project.domain.comment.dto.CommentResponseDto;
import com.forum.project.domain.comment.dtofactory.CommentTestDtoFactory;
import com.forum.project.domain.comment.service.CommentService;
import com.forum.project.testUtils.CustomMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(CustomMockMvc.class)
@WebMvcTest(CommentController.class)
class CommentControllerTest {
    @Autowired
    private CustomMockMvc customMockMvc;

    @MockBean private CommentService commentService;

    private static final String BASE_PATH = "/api/v1/comments";

    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;

    @BeforeEach
    void setUp() {
        commentRequestDto = CommentTestDtoFactory.createCommentRequestDto();
        commentResponseDto = CommentTestDtoFactory.createCommentResponseDto();
    }

    @Test
    void addComment() throws Exception {
        when(commentService.addComment(any(CommentCreateDto.class))).thenReturn(commentResponseDto);

        customMockMvc.postRequest(BASE_PATH + "/{questionId}", commentRequestDto, 1L)
                .andExpect(status().isCreated());

        verify(commentService).addComment(argThat(dto -> dto.getUserId().equals(1L) && dto.getQuestionId().equals(1L)));
    }

    @Test
    void getCommentsByQuestionId() throws Exception {
        when(commentService.getCommentsByQuestionId(anyLong())).thenReturn(List.of(commentResponseDto));

        customMockMvc.getRequest(BASE_PATH + "/{questionId}", 1L)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].loginId").value("testLoginId"));
    }

    @Test
    void getCommentsByUserComments() throws Exception {
        when(commentService.getUserComments(1L)).thenReturn(List.of(commentResponseDto));

        customMockMvc.getRequest(BASE_PATH + "/user")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].loginId").value("testLoginId"));
    }

    @Test
    void getChildComments() throws Exception {
        when(commentService.getChildComments(1L)).thenReturn(List.of(commentResponseDto));

        customMockMvc.getRequest(BASE_PATH + "/child/{parentCommentId}", 1L)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].loginId").value("testLoginId"));
    }

    @Test
    void updateComment() throws Exception {
        customMockMvc.putRequest(BASE_PATH + "/{questionId}/{commentId}", commentRequestDto,2, 3)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        verify(commentService).updateComment(eq(commentRequestDto), argThat(context ->
            context.userId().equals(1L) &&  context.questionId().equals(2L) && context.commentId().equals(3L)));
    }

    @Test
    void deleteComment() throws Exception {
        customMockMvc.deleteRequest(BASE_PATH + "/{questionId}/{commentId}", null, 2L, 3L)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        verify(commentService).deleteComment(argThat(context ->
                context.userId().equals(1L) &&  context.questionId().equals(2L) && context.commentId().equals(3L)));
    }
}