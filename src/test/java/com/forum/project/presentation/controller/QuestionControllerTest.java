//package com.forum.project.presentation.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.forum.project.application.question.QuestionService;
//import com.forum.project.application.security.jwt.TokenService;
//import com.forum.project.presentation.config.TestSecurityConfig;
//import com.forum.project.presentation.question.RequestQuestionDto;
//import com.forum.project.presentation.question.ResponseQuestionDto;
//import com.forum.project.common.utils.ExceptionResponseUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.web.context.request.WebRequest;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//@WebMvcTest(QuestionController.class)
//@Import(TestSecurityConfig.class)
//@ActiveProfiles("test")
//class QuestionControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private QuestionService questionService;
//
//    @MockBean
//    private TokenService tokenService;
//
//    private static final Logger log = LoggerFactory.getLogger(QuestionControllerTest.class);
//
//    private final RequestQuestionDto requestQuestionDto =
//            new RequestQuestionDto("testTitle", "testContent", "testTag");
//
//    private final LocalDateTime dateTime = LocalDateTime.of(2024, 12, 6, 15, 30, 0);
//
//    private final ResponseQuestionDto responseQuestionDto =
//            new ResponseQuestionDto(1L, "testTitle", "testUserId", "testContent", "testTag",
//                    dateTime);
//    @Test
//    @WithMockUser()
//    void test_postQuestion_Success() throws Exception {
//        String accessToken = "access-token";
//        String header = "Bearer " + accessToken;
//
//        when(tokenService.extractTokenByHeader(header)).thenReturn(accessToken);
//        when(questionService.createQuestion(requestQuestionDto, accessToken)).thenReturn(responseQuestionDto);
//
//        mockMvc.perform(post("/api/v1/q/post")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", header)
//                        .content(new ObjectMapper().writeValueAsString(requestQuestionDto)))
//                .andExpect(status().isCreated())
//                .andDo(print())
//                .andExpect(header().string("Content-Type", "application/json"))
//                .andExpect(jsonPath("$.id").value(responseQuestionDto.getId()))
//                .andExpect(jsonPath("$.title").value(responseQuestionDto.getTitle()))
//                .andExpect(jsonPath("$.userId").value(responseQuestionDto.getUserId()))
//                .andExpect(jsonPath("$.content").value(responseQuestionDto.getContent()))
//                .andExpect(jsonPath("$.tag").value(responseQuestionDto.getTag()));
//
//        verify(tokenService).extractTokenByHeader(header);
//        verify(questionService).createQuestion(requestQuestionDto, accessToken);
//    }
//
//    @Test
//    @WithMockUser()
//    void test_updateQuestion_Success() throws Exception {
//        long questionId = 1;
//        String accessToken = "access-token";
//        String header = "Bearer " + accessToken;
//
//        when(tokenService.extractTokenByHeader(header)).thenReturn(accessToken);
//        when(questionService.updateQuestion(questionId, requestQuestionDto, accessToken)).thenReturn(responseQuestionDto);
//
//        mockMvc.perform(put("/api/v1/q/{questionId}", questionId)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .header("Authorization", header)
//                    .content(new ObjectMapper().writeValueAsString(requestQuestionDto)))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andExpect(header().string("Content-Type", "application/json"))
//                .andExpect(jsonPath("$.id").value(responseQuestionDto.getId()))
//                .andExpect(jsonPath("$.title").value(responseQuestionDto.getTitle()))
//                .andExpect(jsonPath("$.userId").value(responseQuestionDto.getUserId()))
//                .andExpect(jsonPath("$.content").value(responseQuestionDto.getContent()))
//                .andExpect(jsonPath("$.tag").value(responseQuestionDto.getTag()));
//
//        verify(tokenService).extractTokenByHeader(header);
//        verify(questionService).updateQuestion(questionId, requestQuestionDto, accessToken);
//    }
//
//    @Test
//    @WithMockUser
//    void test_deleteQuestion_Success() throws Exception {
//        long questionId = 1;
//        String accessToken = "access-token";
//        String header = "Bearer " + accessToken;
//
//        when(tokenService.extractTokenByHeader(header)).thenReturn(accessToken);
//        doNothing().when(questionService).deleteQuestion(questionId, accessToken);
//
//        mockMvc.perform(delete("/api/v1/q/{questionId}", questionId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", header)
//                        .content(new ObjectMapper().writeValueAsString(requestQuestionDto)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(header().string("Content-Type", "application/json"))
//                .andExpect(jsonPath("$.message").value("Question deleted successfully."));
//
//        verify(tokenService).extractTokenByHeader(header);
//        verify(questionService).deleteQuestion(questionId, accessToken);
//    }
//
//    @Test
//    @WithMockUser()
//    void test_getQuestionById_Success() throws Exception {
//        long questionId = 1;
//
//        when(questionService.findById(questionId)).thenReturn(responseQuestionDto);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/q/{id}", questionId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(requestQuestionDto)))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andExpect(header().string("Content-Type", "application/json"))
//                .andExpect(jsonPath("$.id").value(responseQuestionDto.getId()))
//                .andExpect(jsonPath("$.title").value(responseQuestionDto.getTitle()))
//                .andExpect(jsonPath("$.userId").value(responseQuestionDto.getUserId()))
//                .andExpect(jsonPath("$.content").value(responseQuestionDto.getContent()))
//                .andExpect(jsonPath("$.tag").value(responseQuestionDto.getTag()));
//
//        verify(questionService).findById(questionId);
//    }
//
//    @Test
//    @WithMockUser
//    void test_getQuestionsByPage_Success() throws Exception{
//        int page = 0;
//        int size = 10;
//        List<ResponseQuestionDto> questions = List.of(
//                new ResponseQuestionDto(1L, "Test title 1", "testUserId1", "Content1", "tag1", dateTime),
//                new ResponseQuestionDto(2L, "Test title 2", "testUserId2", "Content2", "tag2", dateTime)
//        );
//        PageImpl<ResponseQuestionDto> responsePage = new PageImpl<>(questions, PageRequest.of(page, size), questions.size());
//        when(questionService.getQuestionsByPage(page, size)).thenReturn(responsePage);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/q/questions")
//                        .param("page", String.valueOf(page))
//                        .param("size", String.valueOf(size))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andExpect(header().string("Content-Type", "application/json"))
//                .andExpect(jsonPath("$.content").isArray())
//                .andExpect(jsonPath("$.content[0].title").value("Test title 1"))
//                .andExpect(jsonPath("$.content[1].tag").value("tag2"))
//                .andExpect(jsonPath("$.content[0].createdDate").value(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
//                .andExpect(jsonPath("$.pageable.pageSize").value("10"))
//                .andExpect(jsonPath("$.totalElements").value("2"));
//
//        verify(questionService).getQuestionsByPage(page, size);
//    }
//
//    @Test
//    @WithMockUser
//    void test_getQuestionsSearchPosts_Success() throws Exception{
//        int page = 0;
//        int size = 10;
//        String keyword = "test";
//
//        List<ResponseQuestionDto> questions = List.of(
//                new ResponseQuestionDto(1L, "Test title 1", "testUserId1", "Content1", "tag1", dateTime),
//                new ResponseQuestionDto(2L, "Test title 2", "testUserId2", "Content2", "tag2", dateTime)
//        );
//        PageImpl<ResponseQuestionDto> responsePage = new PageImpl<>(questions, PageRequest.of(page, size), questions.size());
//        when(questionService.searchQuestions(keyword, page, size)).thenReturn(responsePage);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/q/questions")
//                        .param("page", String.valueOf(page))
//                        .param("size", String.valueOf(size))
//                        .param("keyword", keyword)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andExpect(header().string("Content-Type", "application/json"))
//                .andExpect(jsonPath("$.content").isArray())
//                .andExpect(jsonPath("$.content[0].title").value("Test title 1"))
//                .andExpect(jsonPath("$.content[1].tag").value("tag2"))
//                .andExpect(jsonPath("$.content[0].createdDate").value(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
//                .andExpect(jsonPath("$.pageable.pageSize").value("10"))
//                .andExpect(jsonPath("$.totalElements").value("2"));
//
//        verify(questionService).searchQuestions(keyword, page, size);
//    }
//
//}