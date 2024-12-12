package com.forum.project.application.question;

import com.forum.project.application.security.jwt.TokenService;
import com.forum.project.domain.entity.Question;
import com.forum.project.domain.entity.User;
import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.CustomDatabaseException;
import com.forum.project.domain.exception.ErrorCode;
import com.forum.project.domain.repository.QuestionRepository;
import com.forum.project.domain.repository.TotalCountRepository;
import com.forum.project.domain.repository.UserRepository;
import com.forum.project.presentation.dtos.question.QuestionDtoConverter;
import com.forum.project.presentation.dtos.question.RequestQuestionDto;
import com.forum.project.presentation.dtos.question.ResponseQuestionDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import({QuestionService.class})
@ContextConfiguration(classes = QuestionServiceTest.TestConfig.class)
class QuestionServiceTest {

    @Configuration
    static class TestConfig {
        @Bean
        public QuestionDtoConverter questionDtoConverter() {
            return new QuestionDtoConverter();
        }
    }

    @MockBean
    private TotalCountRepository totalCountRepository;

    @MockBean
    private QuestionRepository questionRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private QuestionDtoConverter questionDtoConverter;

    @Autowired
    private QuestionService questionService;

    private final LocalDateTime dateTime = LocalDateTime.of(2024, 12, 6, 15, 30, 0);
    private final RequestQuestionDto requestQuestionDto = new RequestQuestionDto(
            "testTitle", "testContent", "testTag"
    );
    private final ResponseQuestionDto responseQuestionDto = new ResponseQuestionDto(
            1L, "testTitle", "testId","testContent", "testTag", dateTime
    );
    private final User user = User.builder().id(1L).userId("testId").build();
    private final Question question = Question.builder()
            .id(1L)
            .title(requestQuestionDto.getTitle())
            .userId(user.getUserId())
            .content(requestQuestionDto.getContent())
            .tag(requestQuestionDto.getTag())
            .build();
    private final Question savedQuestion = Question.builder()
            .id(1L)
            .title(requestQuestionDto.getTitle())
            .userId(user.getUserId())
            .content(requestQuestionDto.getContent())
            .tag(requestQuestionDto.getTag())
            .build();

    @Test
    void testCreateQuestion_Success() {
        String jwt = "access-token";
        Long id = user.getId();

        when(tokenService.getId(jwt)).thenReturn(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(questionRepository.save(any(Question.class))).thenReturn(savedQuestion);
        doNothing().when(totalCountRepository).incrementTotalCount();

        ArgumentCaptor<Question> questionCaptor = ArgumentCaptor.forClass(Question.class);

        ResponseQuestionDto responseDto = questionService.createQuestion(requestQuestionDto, jwt);

        verify(questionRepository, times(1)).save(questionCaptor.capture());
        Question capturedQuestion = questionCaptor.getValue();

        assertNotNull(responseDto);
        assertEquals(capturedQuestion.getTitle(), requestQuestionDto.getTitle());
        assertEquals(capturedQuestion.getContent(), requestQuestionDto.getContent());
        assertEquals(capturedQuestion.getTag(), requestQuestionDto.getTag());
        assertEquals(capturedQuestion.getUserId(), user.getUserId());
        assertEquals(capturedQuestion.getTitle(), responseDto.getTitle());
        assertEquals(capturedQuestion.getUserId(), responseDto.getUserId());
        assertEquals(capturedQuestion.getContent(), responseDto.getContent());
        assertEquals(capturedQuestion.getTag(), responseDto.getTag());
        verify(userRepository, times(1)).findById(id);
        verify(tokenService, times(1)).getId(jwt);
        verify(totalCountRepository, times(1)).incrementTotalCount();
    }

    @Test
    void testFindById_Success() {
        Long id = 1L;

        when(questionRepository.findById(id)).thenReturn(Optional.of(savedQuestion));

        ResponseQuestionDto responseDto = questionService.findById(id);

        assertNotNull(responseDto);
        assertEquals(savedQuestion.getTitle(), responseDto.getTitle());
        assertEquals(savedQuestion.getUserId(), responseDto.getUserId());
        assertEquals(savedQuestion.getContent(), responseDto.getContent());
        assertEquals(savedQuestion.getTag(), responseDto.getTag());
        verify(questionRepository).findById(id);
    }

    @Test
    void testDeleteQuestion_Success() {
        String jwt = "access-token";
        Long id = 1L;
        String currentUserId = user.getUserId();

        when(tokenService.getUserId(jwt)).thenReturn(currentUserId);
        when(questionRepository.findById(id)).thenReturn(Optional.of(question));
        doNothing().when(questionRepository).deleteById(id);
        doNothing().when(totalCountRepository).decrementTotalCount();

        questionService.deleteQuestion(id, jwt);

        verify(questionRepository, times(1)).findById(id);
        verify(questionRepository, times(1)).deleteById(id);
        verify(tokenService, times(1)).getUserId(jwt);
        verify(totalCountRepository, times(1)).decrementTotalCount();
    }

    @Test
    void testUpdateQuestion_Success() {
        Long id = 1L;
        String jwt = "access-token";
        String currentUserId = user.getUserId();

        when(tokenService.getUserId(jwt)).thenReturn(currentUserId);
        when(questionRepository.findById(id)).thenReturn(Optional.of(question));
        when(questionRepository.save(question)).thenReturn(savedQuestion);

        ResponseQuestionDto responseDto = questionService.updateQuestion(id, requestQuestionDto, jwt);

        assertNotNull(responseDto);
        assertEquals(question.getTitle(), requestQuestionDto.getTitle());
        assertEquals(question.getContent(), requestQuestionDto.getContent());
        assertEquals(question.getTag(), requestQuestionDto.getTag());
        assertEquals(question.getTitle(), responseDto.getTitle());
        assertEquals(question.getContent(), responseDto.getContent());
        assertEquals(question.getTag(), responseDto.getTag());
        assertEquals(savedQuestion.getTitle(), responseDto.getTitle());
        assertEquals(savedQuestion.getUserId(), responseDto.getUserId());
        assertEquals(savedQuestion.getContent(), responseDto.getContent());
        assertEquals(savedQuestion.getTag(), responseDto.getTag());
        verify(tokenService).getUserId(jwt);
        verify(questionRepository).findById(id);
        verify(questionRepository).save(question);
    }

    @Test
    void testGetQuestionsByPage_Success() {
        int page = 0;
        int size = 10;

        List<Question> questions = List.of(
                new Question(1L, "Test title 1", "testUserId1", "Content1", "tag1", dateTime),
                new Question(2L, "Test title 2", "testUserId2", "Content2", "tag2", dateTime)
        );

        when(totalCountRepository.getTotalCount()).thenReturn((long) questions.size());
        when(questionRepository.getQuestionByPage(page, size)).thenReturn(questions);

        Page<ResponseQuestionDto> responseDto = questionService.getQuestionsByPage(page, size);

        assertNotNull(responseDto);
        assertEquals(responseDto.getTotalPages(), 1);
        assertEquals(responseDto.getTotalElements(), 2);
        assertEquals(responseDto.getNumber(), 0);
        assertEquals(responseDto.getNumberOfElements(), 2);
        assertEquals(responseDto.getSize(), 10);
        assertEquals(responseDto.getContent().get(0).getTitle(), "Test title 1");
        verify(totalCountRepository).getTotalCount();
        verify(questionRepository).getQuestionByPage(page, size);
    }

    @Test
    void testSearchQuestions_Success() {
        int page = 0;
        int size = 10;
        String keyword = "test";

        List<Question> questions = List.of(
                new Question(1L, "Test title 1", "testUserId1", "Content1", "tag1", dateTime),
                new Question(2L, "Test title 2", "testUserId2", "Content2", "tag2", dateTime)
        );

        when(totalCountRepository.getTotalCount()).thenReturn((long) questions.size());
        when(questionRepository.searchQuestions(keyword, page, size)).thenReturn(questions);

        Page<ResponseQuestionDto> responseDto = questionService.searchQuestions(keyword, page, size);

        assertNotNull(responseDto);
        assertEquals(responseDto.getTotalPages(), 1);
        assertEquals(responseDto.getTotalElements(), 2);
        assertEquals(responseDto.getNumber(), 0);
        assertEquals(responseDto.getNumberOfElements(), 2);
        assertEquals(responseDto.getSize(), 10);
        assertEquals(responseDto.getContent().get(0).getTitle(), "Test title 1");
        verify(totalCountRepository).getTotalCount();
        verify(questionRepository).searchQuestions(keyword, page, size);
    }

    @Test
    void testCreateQuestion_UserNotFoundException() {
        String jwt = "access-token";
        Long id = user.getId();

        when(tokenService.getId(jwt)).thenReturn(id);
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> questionService.createQuestion(requestQuestionDto, jwt));

        assertEquals(ErrorCode.USER_NOT_FOUND, applicationException.getErrorCode());
        verify(userRepository, times(1)).findById(id);
        verify(tokenService, times(1)).getId(jwt);
    }

    @Test
    void testCreateQuestion_QuestionNotFoundException() {
        String jwt = "access-token";
        Long id = user.getId();

        when(tokenService.getId(jwt)).thenReturn(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(questionRepository.save(any(Question.class))).thenThrow(new CustomDatabaseException("DATABASE ERROR"));

        CustomDatabaseException customDatabaseException = assertThrows(CustomDatabaseException.class,
                () -> questionService.createQuestion(requestQuestionDto, jwt));

        assertEquals(ErrorCode.DATABASE_ERROR, customDatabaseException.getErrorCode());
        assertEquals("DATABASE ERROR", customDatabaseException.getMessage());
        verify(userRepository, times(1)).findById(id);
        verify(tokenService, times(1)).getId(jwt);
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void testFindById_QuestionNotFoundException() {
        Long id = 1L;

        when(questionRepository.findById(id)).thenReturn(Optional.empty());

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> questionService.findById(id));

        assertEquals(ErrorCode.QUESTION_NOT_FOUND, applicationException.getErrorCode());
        verify(questionRepository).findById(id);
    }

    @Test
    void testFindById_DataAccessException() {
        Long id = 1L;

        when(questionRepository.findById(id)).thenThrow(new CustomDatabaseException("DATABASE_ERROR"));

        CustomDatabaseException customDatabaseException = assertThrows(CustomDatabaseException.class,
                () -> questionService.findById(id));

        assertEquals(ErrorCode.DATABASE_ERROR, customDatabaseException.getErrorCode());
        verify(questionRepository).findById(id);
    }

    @Test
    void testDeleteQuestion_QuestionNotFoundException() {
        String jwt = "access-token";
        Long id = 1L;
        String currentUserId = user.getUserId();

        when(tokenService.getUserId(jwt)).thenReturn(currentUserId);
        when(questionRepository.findById(id)).thenReturn(Optional.empty());

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> questionService.deleteQuestion(id, jwt));

        assertEquals(ErrorCode.QUESTION_NOT_FOUND, applicationException.getErrorCode());
        verify(tokenService, times(1)).getUserId(jwt);
        verify(questionRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteQuestion_BadCredentialException() {
        String jwt = "access-token";
        Long id = 1L;
        String currentUserId = user.getUserId();

        Question unequalUserIdQuestion = Question.builder()
                        .userId("unequalId")
                        .build();

        when(tokenService.getUserId(jwt)).thenReturn(currentUserId);
        when(questionRepository.findById(id)).thenReturn(Optional.of(unequalUserIdQuestion));

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> questionService.deleteQuestion(id, jwt));

        assertEquals(ErrorCode.AUTH_BAD_CREDENTIAL, applicationException.getErrorCode());
        verify(tokenService, times(1)).getUserId(jwt);
        verify(questionRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteQuestion_DataAccessException() {
        String jwt = "access-token";
        Long id = 1L;
        String currentUserId = user.getUserId();

        when(tokenService.getUserId(jwt)).thenReturn(currentUserId);
        when(questionRepository.findById(id)).thenReturn(Optional.of(question));
        doThrow(new CustomDatabaseException("DATABASE ERROR"))
                .when(questionRepository).deleteById(id);

        CustomDatabaseException customDatabaseException = assertThrows(CustomDatabaseException.class,
                () -> questionService.deleteQuestion(id, jwt));

        assertEquals(ErrorCode.DATABASE_ERROR, customDatabaseException.getErrorCode());
        verify(tokenService, times(1)).getUserId(jwt);
        verify(questionRepository, times(1)).findById(id);
    }

    @Test
    void testUpdateQuestion_QuestionNotFoundException() {
        String jwt = "access-token";
        Long id = 1L;
        String currentUserId = user.getUserId();

        when(tokenService.getUserId(jwt)).thenReturn(currentUserId);
        when(questionRepository.findById(id)).thenReturn(Optional.empty());

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> questionService.updateQuestion(id, requestQuestionDto, jwt));

        assertEquals(ErrorCode.QUESTION_NOT_FOUND, applicationException.getErrorCode());
        verify(tokenService, times(1)).getUserId(jwt);
        verify(questionRepository, times(1)).findById(id);
    }

}