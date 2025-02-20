package com.forum.project.domain.bookmark.controller;

import com.forum.project.domain.auth.service.TokenService;
import com.forum.project.domain.bookmark.service.QuestionBookmarkService;
import com.forum.project.presentation.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@WebMvcTest(QuestionBookmarkController.class)
public class QuestionBookmarkControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionBookmarkService questionBookmarkService;
    @MockBean
    private TokenService tokenService;

    private static final String BASE_PATH = "/api/v1/questions/bookmarks";
    private static final String AUTHORIZATION_HEADER = "Bearer token";

    @BeforeEach
    void setUp() {
        when(tokenService.getUserId("token")).thenReturn(1L);
    }

    @Test
    void saveQuestionBookmark() throws Exception {
        mockMvc.perform(post(BASE_PATH + "/{id}", 1L)
                        .header("Authorization", AUTHORIZATION_HEADER))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testDeleteQuestionBookmark() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/{id}", 1L)
                        .header("Authorization", AUTHORIZATION_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testGetQuestionBookmarks() throws Exception {
        when(questionBookmarkService.getUserBookmarks(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get(BASE_PATH)
                        .header("Authorization", AUTHORIZATION_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}