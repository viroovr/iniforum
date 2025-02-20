package com.forum.project.presentation.question;

import com.forum.project.domain.question.service.QuestionQueryService;
import com.forum.project.domain.question.vo.QuestionSortType;
import com.forum.project.domain.auth.service.AuthorizationService;
import com.forum.project.domain.question.controller.QuestionQueryController;
import com.forum.project.domain.question.vo.QuestionStatus;
import com.forum.project.presentation.config.TestSecurityConfig;
import com.forum.project.domain.question.dto.QuestionPageResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = QuestionQueryController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class QuestionQueryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionQueryService questionQueryService;
    @MockBean
    private AuthorizationService authorizationService;

    @Test
    void testGetQuestionsByPage_success() throws Exception{
        int page = 0;
        int size = 10;
        List<QuestionPageResponseDto> dtoList = List.of(
                QuestionPageResponseDto.builder().questionId(1L).build(),
                QuestionPageResponseDto.builder().questionId(2L).build()
        );
        Pageable pageable = PageRequest.of(page, size);
        PageImpl<QuestionPageResponseDto> responseDtoPage = new PageImpl<>(dtoList, pageable, 11L);

        when(questionQueryService.readQuestionsByPage(page, size)).thenReturn(responseDtoPage);

        mockMvc.perform(get("/api/v1/questions/query")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content[0].questionId").value(1L))
                .andExpect(jsonPath("$.content[1].questionId").value(2L));
    }

    @Test
    void testGetQuestionsSortedBy_success() throws Exception{
        QuestionSortType questionSortType = QuestionSortType.LATEST;
        int page = 0;
        int size = 10;
        List<QuestionPageResponseDto> dtoList = List.of(
                QuestionPageResponseDto.builder().questionId(1L).build(),
                QuestionPageResponseDto.builder().questionId(2L).build()
        );
        Pageable pageable = PageRequest.of(page, size, questionSortType.getSort());
        PageImpl<QuestionPageResponseDto> responseDtoPage = new PageImpl<>(dtoList, pageable, dtoList.size());

        when(questionQueryService.readQuestionsSortedBy(any(QuestionSortType.class), eq(page), eq(size)))
                .thenReturn(responseDtoPage);

        mockMvc.perform(get("/api/v1/questions/query/sorted")
                        .param("sortType", questionSortType.name())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content[0].questionId").value(1L))
                .andExpect(jsonPath("$.content[1].questionId").value(2L))
                .andExpect(jsonPath("$.sort.sorted").value(true));
    }

    @Test
    void testGetQuestionsByKeyword_success() throws Exception{
        String keyword = "test";
        int page = 0;
        int size = 10;
        List<QuestionPageResponseDto> dtoList = List.of(
                QuestionPageResponseDto.builder().questionId(1L).title("test1").build(),
                QuestionPageResponseDto.builder().questionId(2L).title("test2").build()
        );
        Pageable pageable = PageRequest.of(page, size);
        PageImpl<QuestionPageResponseDto> responseDtoPage = new PageImpl<>(dtoList, pageable, dtoList.size());

        when(questionQueryService.readQuestionsByKeyword(keyword, page, size)).thenReturn(responseDtoPage);

        mockMvc.perform(get("/api/v1/questions/query/search")
                        .param("keyword", keyword)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content[0].questionId").value(1L))
                .andExpect(jsonPath("$.content[1].questionId").value(2L))
                .andExpect(jsonPath("$.content[0].title").value("test1"))
                .andExpect(jsonPath("$.content[1].title").value("test2"));
    }

    @Test
    void testGetQuestionsByStatus_success() throws Exception{
        QuestionStatus questionStatus = QuestionStatus.OPEN;
        int page = 0;
        int size = 10;
        List<QuestionPageResponseDto> dtoList = List.of(
                QuestionPageResponseDto.builder().questionId(1L).status(questionStatus.name()).build(),
                QuestionPageResponseDto.builder().questionId(2L).status(questionStatus.name()).build()
        );
        Pageable pageable = PageRequest.of(page, size);
        PageImpl<QuestionPageResponseDto> responseDtoPage = new PageImpl<>(dtoList, pageable, dtoList.size());

        when(questionQueryService.readQuestionsByStatus(any(QuestionStatus.class), eq(page), eq(size)))
                .thenReturn(responseDtoPage);

        mockMvc.perform(get("/api/v1/questions/query/status")
                        .param("status", questionStatus.name())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content[0].questionId").value(1L))
                .andExpect(jsonPath("$.content[1].questionId").value(2L))
                .andExpect(jsonPath("$.content[0].status").value(questionStatus.name()))
                .andExpect(jsonPath("$.content[1].status").value(questionStatus.name()));
    }

    @Test
    void testGetQuestionsByUserId_success() throws Exception{
        Long userId = 1L;
        int page = 0;
        int size = 10;
        List<QuestionPageResponseDto> dtoList = List.of(
                QuestionPageResponseDto.builder().questionId(1L).build(),
                QuestionPageResponseDto.builder().questionId(2L).build()
        );
        Pageable pageable = PageRequest.of(page, size);
        PageImpl<QuestionPageResponseDto> responseDtoPage = new PageImpl<>(dtoList, pageable, dtoList.size());

        when(questionQueryService.readQuestionsByUserId(userId, page, size)).thenReturn(responseDtoPage);

        mockMvc.perform(get("/api/v1/questions/query/users/{userId}", userId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content[0].questionId").value(1L))
                .andExpect(jsonPath("$.content[1].questionId").value(2L));
    }

    @Test
    void testGetQuestionsByTag_success() throws Exception{
        String tag = "test";
        int page = 0;
        int size = 10;
        List<QuestionPageResponseDto> dtoList = List.of(
                QuestionPageResponseDto.builder().questionId(1L).tags(List.of(tag)).build(),
                QuestionPageResponseDto.builder().questionId(2L).tags(List.of(tag)).build()
        );
        Pageable pageable = PageRequest.of(page, size);
        PageImpl<QuestionPageResponseDto> responseDtoPage = new PageImpl<>(dtoList, pageable, dtoList.size());

        when(questionQueryService.readQuestionsByTag(tag, page, size)).thenReturn(responseDtoPage);

        mockMvc.perform(get("/api/v1/questions/query/tags/{tag}", tag)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content[0].questionId").value(1L))
                .andExpect(jsonPath("$.content[1].questionId").value(2L))
                .andExpect(jsonPath("$.content[0].tags[0]").value(tag));
    }
}
