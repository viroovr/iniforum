//package com.forum.project.presentation.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.forum.project.application.jwt.TokenService;
//import com.forum.project.application.question.QuestionService;
//import com.forum.project.application.user.auth.AuthenticationService;
//import com.forum.project.domain.user.User;
//import com.forum.project.domain.user.UserRepository;
//import com.forum.project.presentation.config.TestSecurityConfig;
//import com.forum.project.presentation.question.QuestionController;
//import com.forum.project.presentation.question.dto.QuestionRequestDto;
//import com.forum.project.presentation.question.dto.QuestionResponseDto;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.mock.mockito.SpyBean;
//import org.springframework.context.annotation.EnableAspectJAutoProxy;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(MockitoExtension.class)
//@WebMvcTest(QuestionController.class)
//@Import(TestSecurityConfig.class)
//@ActiveProfiles("test")
//@EnableAspectJAutoProxy
//class QuestionControllerTest {
//
//    @SpyBean
//    private AuthenticationService authenticationService;
//
//    @MockBean
//    private TokenService tokenService;  // TokenService 모킹
//
//    @MockBean
//    private UserRepository userRepository;  // UserRepository 모킹
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private QuestionService questionService;
//
//
//    private final LocalDateTime dateTime = LocalDateTime.of(2024, 12, 6, 15, 30, 0);
//
//    @Test
//    void test_postQuestion_Success() throws Exception {
//        QuestionRequestDto questionRequestDto = QuestionRequestDto.builder()
//                .title("testTitle")
//                .content("testContent")
//                .build();
//        String header = "Bearer accessToken";
//        User user = User.builder().id(1L).build();
//        QuestionResponseDto questionResponseDto = QuestionResponseDto.builder()
//                        .loginId("userLoginId")
//                        .content("testContent")
//                        .title("testTitle").build();
//
//        when(tokenService.getUserId("accessToken")).thenReturn(user.getId());
//        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
//        when(authenticationService.extractUserByToken(header)).thenReturn(user);
//        when(questionService.createQuestion(eq(questionRequestDto), eq(user))).thenReturn(questionResponseDto);
//
//        mockMvc.perform(post("/api/v1/q/post")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", header)
//                        .content(new ObjectMapper().writeValueAsString(questionRequestDto)))
//                .andExpect(status().isCreated())
//                .andDo(print());
////                .andExpect(jsonPath("$.title").value("testTitle"))
////                .andExpect(jsonPath("$.loginId").value("userLoginId"))
////                .andExpect(jsonPath("$.content").value("testContent"));
//        verify(authenticationService).extractUserByToken(header);
//        verify(questionService).createQuestion(questionRequestDto, user);
//    }
//
////    @Test
////    @WithMockUser()
////    void test_updateQuestion_Success() throws Exception {
////        long questionId = 1;
////        String accessToken = "access-token";
////        String header = "Bearer " + accessToken;
////
////        when(tokenService.extractTokenByHeader(header)).thenReturn(accessToken);
////        when(questionService.updateQuestion(questionId, requestQuestionDto, accessToken)).thenReturn(responseQuestionDto);
////
////        mockMvc.perform(put("/api/v1/q/{questionId}", questionId)
////                    .contentType(MediaType.APPLICATION_JSON)
////                    .header("Authorization", header)
////                    .content(new ObjectMapper().writeValueAsString(requestQuestionDto)))
////                .andExpect(status().isOk())
////                .andDo(print())
////                .andExpect(header().string("Content-Type", "application/json"))
////                .andExpect(jsonPath("$.id").value(responseQuestionDto.getId()))
////                .andExpect(jsonPath("$.title").value(responseQuestionDto.getTitle()))
////                .andExpect(jsonPath("$.userId").value(responseQuestionDto.getUserId()))
////                .andExpect(jsonPath("$.content").value(responseQuestionDto.getContent()))
////                .andExpect(jsonPath("$.tag").value(responseQuestionDto.getTag()));
////
////        verify(tokenService).extractTokenByHeader(header);
////        verify(questionService).updateQuestion(questionId, requestQuestionDto, accessToken);
////    }
////
////    @Test
////    @WithMockUser
////    void test_deleteQuestion_Success() throws Exception {
////        long questionId = 1;
////        String accessToken = "access-token";
////        String header = "Bearer " + accessToken;
////
////        when(tokenService.extractTokenByHeader(header)).thenReturn(accessToken);
////        doNothing().when(questionService).deleteQuestion(questionId, accessToken);
////
////        mockMvc.perform(delete("/api/v1/q/{questionId}", questionId)
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .header("Authorization", header)
////                        .content(new ObjectMapper().writeValueAsString(requestQuestionDto)))
////                .andDo(print())
////                .andExpect(status().isOk())
////                .andExpect(header().string("Content-Type", "application/json"))
////                .andExpect(jsonPath("$.message").value("Question deleted successfully."));
////
////        verify(tokenService).extractTokenByHeader(header);
////        verify(questionService).deleteQuestion(questionId, accessToken);
////    }
////
////    @Test
////    @WithMockUser()
////    void test_getQuestionById_Success() throws Exception {
////        long questionId = 1;
////
////        when(questionService.findById(questionId)).thenReturn(responseQuestionDto);
////
////        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/q/{id}", questionId)
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(new ObjectMapper().writeValueAsString(requestQuestionDto)))
////                .andExpect(status().isOk())
////                .andDo(print())
////                .andExpect(header().string("Content-Type", "application/json"))
////                .andExpect(jsonPath("$.id").value(responseQuestionDto.getId()))
////                .andExpect(jsonPath("$.title").value(responseQuestionDto.getTitle()))
////                .andExpect(jsonPath("$.userId").value(responseQuestionDto.getUserId()))
////                .andExpect(jsonPath("$.content").value(responseQuestionDto.getContent()))
////                .andExpect(jsonPath("$.tag").value(responseQuestionDto.getTag()));
////
////        verify(questionService).findById(questionId);
////    }
////
////    @Test
////    @WithMockUser
////    void test_getQuestionsByPage_Success() throws Exception{
////        int page = 0;
////        int size = 10;
////        List<ResponseQuestionDto> questions = List.of(
////                new ResponseQuestionDto(1L, "Test title 1", "testUserId1", "Content1", "tag1", dateTime),
////                new ResponseQuestionDto(2L, "Test title 2", "testUserId2", "Content2", "tag2", dateTime)
////        );
////        PageImpl<ResponseQuestionDto> responsePage = new PageImpl<>(questions, PageRequest.of(page, size), questions.size());
////        when(questionService.getQuestionsByPage(page, size)).thenReturn(responsePage);
////
////        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/q/questions")
////                        .param("page", String.valueOf(page))
////                        .param("size", String.valueOf(size))
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk())
////                .andDo(print())
////                .andExpect(header().string("Content-Type", "application/json"))
////                .andExpect(jsonPath("$.content").isArray())
////                .andExpect(jsonPath("$.content[0].title").value("Test title 1"))
////                .andExpect(jsonPath("$.content[1].tag").value("tag2"))
////                .andExpect(jsonPath("$.content[0].createdDate").value(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
////                .andExpect(jsonPath("$.pageable.pageSize").value("10"))
////                .andExpect(jsonPath("$.totalElements").value("2"));
////
////        verify(questionService).getQuestionsByPage(page, size);
////    }
////
////    @Test
////    @WithMockUser
////    void test_getQuestionsSearchPosts_Success() throws Exception{
////        int page = 0;
////        int size = 10;
////        String keyword = "test";
////
////        List<ResponseQuestionDto> questions = List.of(
////                new ResponseQuestionDto(1L, "Test title 1", "testUserId1", "Content1", "tag1", dateTime),
////                new ResponseQuestionDto(2L, "Test title 2", "testUserId2", "Content2", "tag2", dateTime)
////        );
////        PageImpl<ResponseQuestionDto> responsePage = new PageImpl<>(questions, PageRequest.of(page, size), questions.size());
////        when(questionService.searchQuestions(keyword, page, size)).thenReturn(responsePage);
////
////        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/q/questions")
////                        .param("page", String.valueOf(page))
////                        .param("size", String.valueOf(size))
////                        .param("keyword", keyword)
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk())
////                .andDo(print())
////                .andExpect(header().string("Content-Type", "application/json"))
////                .andExpect(jsonPath("$.content").isArray())
////                .andExpect(jsonPath("$.content[0].title").value("Test title 1"))
////                .andExpect(jsonPath("$.content[1].tag").value("tag2"))
////                .andExpect(jsonPath("$.content[0].createdDate").value(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
////                .andExpect(jsonPath("$.pageable.pageSize").value("10"))
////                .andExpect(jsonPath("$.totalElements").value("2"));
////
////        verify(questionService).searchQuestions(keyword, page, size);
////    }
//
//}