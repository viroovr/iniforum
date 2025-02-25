package com.forum.project.domain.report.controller;

import com.forum.project.domain.comment.service.CommentService;
import com.forum.project.domain.report.dto.ReportRequestDto;
import com.forum.project.presentation.dtos.TestDtoFactory;
import com.forum.project.testUtils.CustomMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(CustomMockMvc.class)
@WebMvcTest(CommentReportController.class)
class CommentReportControllerTest {
    @Autowired
    private CustomMockMvc customMockMvc;

    @MockBean private CommentService commentService;

    private static final String BASE_PATH = "/api/v1/comments/report";
    private ReportRequestDto reportRequestDto;

    @BeforeEach
    void setUp() {
        reportRequestDto = TestDtoFactory.createReportRequestDto();
    }

    @Test
    void reportComment() throws Exception {
        customMockMvc.postRequest(BASE_PATH + "/{questionId}/{commentId}", reportRequestDto, 2, 3)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        verify(commentService).reportComment(eq(reportRequestDto), argThat(context ->
            context.questionId().equals(2L) && context.commentId().equals(3L) && context.userId().equals(1L)
        ));
    }
}