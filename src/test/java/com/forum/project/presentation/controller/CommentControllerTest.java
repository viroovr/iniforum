package com.forum.project.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.project.application.comment.CommentService;
import com.forum.project.application.jwt.TokenService;
import com.forum.project.presentation.comment.CommentController;
import com.forum.project.presentation.config.TestSecurityConfig;
import com.forum.project.presentation.comment.CommentRequestDto;
import com.forum.project.presentation.comment.CommentResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import(TestSecurityConfig.class)
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private TokenService tokenService;

    private final LocalDateTime dateTime = LocalDateTime.of(2024, 12, 16, 12, 30, 0);
    private final CommentRequestDto commentRequestDto = new CommentRequestDto("testContent");
    private final CommentResponseDto commentResponseDto = new CommentResponseDto(1L, "testContent", "testId", dateTime, 0L);

    @Test
    @WithMockUser
    void testAddComment_Success() throws Exception {
        String accessToken = "access-token";
        String header = "Bearer " + accessToken;
        Long questionId = 1L;
        when(tokenService.extractTokenByHeader(header)).thenReturn(accessToken);
        when(commentService.addComment(questionId, commentRequestDto, accessToken)).thenReturn(commentResponseDto);

        mockMvc.perform(post("/api/v1/comments/{questionId}", questionId)
                    .header("Authorization", header)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(commentRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponseDto.getId()))
                .andExpect(jsonPath("$.content").value(commentResponseDto.getContent()));

        verify(tokenService).extractTokenByHeader(header);
        verify(commentService).addComment(questionId, commentRequestDto, accessToken);
    }

    @Test
    @WithMockUser
    void testGetComments_Success() throws Exception{
        Long questionId = 1L;
        List<CommentResponseDto> comments = List.of(
                commentResponseDto,
                new CommentResponseDto(2L, "testContent2", "testId2", dateTime, 0L)
        );
        when(commentService.getCommentsByQuestionId(questionId)).thenReturn(comments);

        mockMvc.perform(get("/api/v1/comments/{questionId}", questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(comments.size()))
                .andExpect(jsonPath("$[0].content").value(comments.get(0).getContent()))
                .andExpect(jsonPath("$[1].content").value(comments.get(1).getContent()));

        verify(commentService).getCommentsByQuestionId(questionId);
    }

    @Test
    @WithMockUser
    void testUpdateComment_Success() throws Exception{
        String accessToken = "access-token";
        String header = "Bearer " + accessToken;
        Long commentId = 1L;
        when(tokenService.extractTokenByHeader(header)).thenReturn(accessToken);
        when(commentService.updateComment(commentId, commentRequestDto, accessToken)).thenReturn(commentResponseDto);

        mockMvc.perform(put("/api/v1/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentRequestDto))
                        .header("Authorization", header))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(commentRequestDto.getContent()));
    
        verify(tokenService).extractTokenByHeader(header);
        verify(commentService).updateComment(commentId, commentRequestDto, accessToken);
    }

    @Test
    @WithMockUser
    void testDeleteComment_Success() throws Exception{
        String accessToken = "access-token";
        String header = "Bearer " + accessToken;
        Long commentId = 1L;
        when(tokenService.extractTokenByHeader(header)).thenReturn(accessToken);
        doNothing().when(commentService).deleteComment(commentId, accessToken);

        mockMvc.perform(delete("/api/v1/comments/{commentId}", commentId)
                        .header("Authorization", header))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Comment deleted successfully."));

        verify(tokenService).extractTokenByHeader(header);
        verify(commentService).deleteComment(commentId, accessToken);
    }

    @Test
    @WithMockUser
    void testLikeComment_Success() throws Exception{
        String accessToken = "access-token";
        String header = "Bearer " + accessToken;
        Long commentId = 1L;
        when(tokenService.extractTokenByHeader(header)).thenReturn(accessToken);
        doNothing().when(commentService).likeComment(commentId, accessToken);

        mockMvc.perform(post("/api/v1/comments/{commentId}/like", commentId)
                        .header("Authorization", header))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Comment liked successfully."));

        verify(tokenService).extractTokenByHeader(header);
        verify(commentService).likeComment(commentId, accessToken);
    }
}