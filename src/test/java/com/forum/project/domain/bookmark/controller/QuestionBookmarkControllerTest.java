package com.forum.project.domain.bookmark.controller;

import com.forum.project.domain.bookmark.dto.BookmarkRequestDto;
import com.forum.project.domain.bookmark.entity.Bookmark;
import com.forum.project.domain.bookmark.service.QuestionBookmarkService;
import com.forum.project.presentation.dtos.TestDtoFactory;
import com.forum.project.testUtils.CustomMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(CustomMockMvc.class)
@WebMvcTest(QuestionBookmarkController.class)
public class QuestionBookmarkControllerTest {
    @Autowired
    private CustomMockMvc customMockMvc;

    @MockBean
    private QuestionBookmarkService questionBookmarkService;

    private static final String BASE_PATH = "/api/v1/questions/bookmarks";

    private BookmarkRequestDto bookmarkRequestDto;
    private Bookmark bookmark;

    @BeforeEach
    void setUp() {
        bookmark = TestDtoFactory.createBookmark();
        bookmarkRequestDto = TestDtoFactory.createBookmarkRequestDto();
    }

    @Test
    void saveQuestionBookmark() throws Exception {
        when(questionBookmarkService.saveQuestionBookmark(bookmarkRequestDto)).thenReturn(bookmark);

        customMockMvc.postRequest(BASE_PATH + "/save", bookmarkRequestDto)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deleteQuestionBookmark() throws Exception {
        customMockMvc.deleteRequest(BASE_PATH + "/{id}",null, 1L)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getQuestionBookmarks() throws Exception {
        when(questionBookmarkService.getUserBookmarks(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        customMockMvc.getRequest(BASE_PATH)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}