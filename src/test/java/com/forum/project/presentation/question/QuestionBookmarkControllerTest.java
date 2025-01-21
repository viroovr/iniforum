package com.forum.project.presentation.question;

import com.forum.project.application.question.QuestionBookmarkService;
import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.domain.bookmark.Bookmark;
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

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(QuestionBookmarkController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class QuestionBookmarkControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionBookmarkService questionBookmarkService;
    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void testSaveQuestionBookmark_success() throws Exception {
        Long questionId = 1L;
        Long userId = 1L;
        String header = "Bearer accessToken";

        when(authenticationService.extractUserId(header)).thenReturn(userId);
        doNothing().when(questionBookmarkService).saveQuestionBookmark(questionId, userId);

        mockMvc.perform(post("/api/v1/questions/bookmarks/{id}", questionId)
                        .header("Authorization", header))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Bookmark successfully"));
    }

    @Test
    void testDeleteQuestionBookmark_success() throws Exception {
        Long questionId = 1L;
        Long userId = 1L;
        String header = "Bearer accessToken";

        when(authenticationService.extractUserId(header)).thenReturn(userId);
        doNothing().when(questionBookmarkService).removeBookmark(questionId, userId);

        mockMvc.perform(delete("/api/v1/questions/bookmarks/{id}", questionId)
                        .header("Authorization", header))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Delete Bookmark successfully"));
    }

    @Test
    void testGetQuestionBookmarks_success() throws Exception {
        Long userId = 1L;
        String header = "Bearer accessToken";
        List<Bookmark> bookmarks = List.of(
                Bookmark.builder().questionId(1L).userId(userId).build(),
                Bookmark.builder().questionId(2L).userId(userId).build()
        );
        when(authenticationService.extractUserId(header)).thenReturn(userId);
        when(questionBookmarkService.getUserBookmarks(userId)).thenReturn(bookmarks);

        mockMvc.perform(get("/api/v1/questions/bookmarks")
                        .header("Authorization", header))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].questionId").value(1L))
                .andExpect(jsonPath("$[1].questionId").value(2L));
    }
}