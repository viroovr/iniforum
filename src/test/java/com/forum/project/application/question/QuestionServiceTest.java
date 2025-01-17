package com.forum.project.application.question;

import com.forum.project.application.bookmark.QuestionBookmarkService;
import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.domain.report.ReportRequestDto;
import com.forum.project.presentation.question.dto.QuestionPageResponseDto;
import com.forum.project.presentation.question.dto.QuestionRequestDto;
import com.forum.project.presentation.question.dto.QuestionResponseDto;
import com.forum.project.presentation.tag.TagRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class QuestionServiceTest {
    @InjectMocks
    private QuestionService questionService;

    @Mock
    private QuestionCrudService questionCrudService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private QuestionReportService questionReportService;

    @Mock
    private QuestionLikeService questionLikeService;

    @Mock
    private QuestionBookmarkService questionBookmarkService;

    private QuestionRequestDto requestDto;

    @BeforeEach
    void setUp() {
        TagRequestDto tagRequestDto = new TagRequestDto(List.of("testTag1", "testTag2"), "testCategory");
        requestDto = QuestionRequestDto.builder()
                .title("testTitle").content("testContent")
                .tagRequestDto(tagRequestDto)
                .build();
    }

       @Test
    void testVoteUpQuestion_success() {
        Long questionId = 1L;
        String token = "accessToken";

        Long userId = 1L;
        QuestionResponseDto responseDto = QuestionResponseDto.builder()
                .questionId(1L).title(requestDto.getTitle()).content(requestDto.getContent())
                .build();

        doNothing().when(questionLikeService).addLike(questionId, userId);
        when(authenticationService.extractUserId(token)).thenReturn(userId);
        when(questionCrudService.updateUpVotedCount(questionId, userId)).thenReturn(responseDto);

        QuestionResponseDto result = questionService.voteUpQuestion(questionId, token);

        assertNotNull(result);
        assertEquals(responseDto.getQuestionId(), result.getQuestionId());
        assertEquals(responseDto.getTitle(), result.getTitle());
        assertEquals(responseDto.getContent(), result.getContent());
    }

    @Test
    void testGetQuestionsByPage_success() {
        int page = 0;
        int size = 10;

        Page<QuestionPageResponseDto> responseDto = new PageImpl<>(List.of(
                QuestionPageResponseDto.builder().questionId(1L).title("testTitle").loginId("testLoginId").build()));

        when(questionCrudService.readQuestionsByPage(page, size)).thenReturn(responseDto);

        Page<QuestionPageResponseDto> result = questionService.getQuestionsByPage(page, size);

        assertNotNull(result);
        assertEquals(responseDto.getContent().get(0).getQuestionId(), result.getContent().get(0).getQuestionId());
        assertEquals(responseDto.getContent().get(0).getTitle(), result.getContent().get(0).getTitle());
    }

    @Test
    void testReportQuestion_success() {
        Long questionId = 1L;
        String token = "accessToken";

        Long userId = 1L;

        ReportRequestDto reportRequestDto = new ReportRequestDto("testReason");

        when(authenticationService.extractUserId(token)).thenReturn(userId);
        when(questionCrudService.existsQuestion(questionId)).thenReturn(false);
        doNothing().when(questionReportService).saveReport(questionId, userId, reportRequestDto.getReason());

        questionService.reportQuestion(questionId, reportRequestDto, token);

        verify(questionCrudService).existsQuestion(questionId);
        verify(questionReportService).saveReport(questionId, userId, reportRequestDto.getReason());
    }

    @Test
    void testBookmarkQuestion_success() {
        Long questionId = 1L;
        String token = "accessToken";

        Long userId = 1L;

        when(authenticationService.extractUserId(token)).thenReturn(userId);
        doNothing().when(questionBookmarkService).bookmarkQuestion(questionId, userId);

        questionService.bookmarkQuestion(questionId, token);

        verify(questionBookmarkService).bookmarkQuestion(questionId, userId);
    }
}