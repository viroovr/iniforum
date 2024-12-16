package com.forum.project.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.project.application.question.CommentService;
import com.forum.project.application.security.jwt.TokenService;
import com.forum.project.presentation.config.TestSecurityConfig;
import com.forum.project.presentation.dtos.comment.RequestCommentDto;
import com.forum.project.presentation.dtos.comment.ResponseCommentDto;
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
    private final RequestCommentDto requestCommentDto = new RequestCommentDto("testContent");
    private final ResponseCommentDto responseCommentDto = new ResponseCommentDto(1L, "testContent", "testId", dateTime, 0L);

    @Test
    @WithMockUser
    void testAddComment_Success() throws Exception {
        String accessToken = "access-token";
        String header = "Bearer " + accessToken;
        Long questionId = 1L;
        when(tokenService.extractTokenByHeader(header)).thenReturn(accessToken);
        when(commentService.addComment(questionId, requestCommentDto, accessToken)).thenReturn(responseCommentDto);

        mockMvc.perform(post("/api/v1/comments/{questionId}", questionId)
                    .header("Authorization", header)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(requestCommentDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseCommentDto.getId()))
                .andExpect(jsonPath("$.content").value(responseCommentDto.getContent()));

        verify(tokenService).extractTokenByHeader(header);
        verify(commentService).addComment(questionId, requestCommentDto, accessToken);
    }

    @Test
    @WithMockUser
    void testGetComments_Success() throws Exception{
        Long questionId = 1L;
        List<ResponseCommentDto> comments = List.of(
                responseCommentDto,
                new ResponseCommentDto(2L, "testContent2", "testId2", dateTime, 0L)
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
        when(commentService.updateComment(commentId, requestCommentDto, accessToken)).thenReturn(responseCommentDto);

        mockMvc.perform(put("/api/v1/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestCommentDto))
                        .header("Authorization", header))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(requestCommentDto.getContent()));
    
        verify(tokenService).extractTokenByHeader(header);
        verify(commentService).updateComment(commentId, requestCommentDto, accessToken);
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