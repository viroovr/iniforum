package com.forum.project.application.question;

import com.forum.project.domain.tag.service.TagService;
import com.forum.project.domain.question.entity.Question;
import com.forum.project.domain.question.service.QuestionCrudService;
import com.forum.project.domain.question.service.QuestionViewCountService;
import com.forum.project.domain.question.validator.QuestionValidator;
import com.forum.project.infrastructure.persistence.key.QuestionKey;
import com.forum.project.domain.question.repository.QuestionRepository;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.question.dto.QuestionCreateDto;
import com.forum.project.domain.question.dto.QuestionResponseDto;
import com.forum.project.domain.tag.dto.TagRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionCrudServiceTest {
    @InjectMocks
    private QuestionCrudService questionCrudService;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private TagService tagService;
    @Mock
    private QuestionViewCountService questionViewCountService;
    @Mock
    private QuestionValidator questionValidator;

    private Map<String, Object> generateKeys(Long id, Timestamp timestamp) {
        Map<String, Object> generatedKeys = new HashMap<>();
        generatedKeys.put(QuestionKey.ID, id);
        generatedKeys.put(QuestionKey.CREATED_DATE, timestamp);
        generatedKeys.put(QuestionKey.LAST_MODIFIED_DATE, timestamp);
        return generatedKeys;
    }

    @Test
    void testCreate_success() {
        List<String> stringTags = List.of("tag1", "tag2");
        QuestionCreateDto questionCreateDto = QuestionCreateDto.builder()
                .content("testContent")
                .title("testTitle")
                .tagRequestDto(new TagRequestDto(stringTags))
                .user(User.builder().id(1L).loginId("loginId").build())
                .build();
        Question savedQuestion = Question.builder()
                .id(1L)
                .content("testContent")
                .title("testTitle")
                .userId(1L).build();
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        Map<String, Object> keys = generateKeys(1L, timestamp);

        when(questionRepository.insertAndReturnGeneratedKeys(any(Question.class))).thenReturn(keys);
        when(tagService.createAndAttachTagsToQuestion(
                questionCreateDto.getTagRequestDto(), savedQuestion.getId())).thenReturn(stringTags);

        QuestionResponseDto response = questionCrudService.create(questionCreateDto);

        assertNotNull(response);
        assertEquals(savedQuestion.getId(), response.getQuestionId());
        assertEquals(savedQuestion.getContent(), response.getContent());
        assertEquals(savedQuestion.getTitle(), response.getTitle());
        assertEquals(stringTags, response.getTags());
    }

    @Test
    void testReadQuestion_success() {
        Long questionId = 1L;
        Long userId = 1L;
        Question question = Question.builder()
                .id(questionId)
                .userId(userId)
                .title("testTitle")
                .build();
        List<String> stringTags = List.of("tag1", "tag2");
        when(questionRepository.findById(questionId)).thenReturn(Optional.ofNullable(question));
        when(tagService.getStringTagsByQuestionId(questionId)).thenReturn(stringTags);
        doNothing().when(questionViewCountService).incrementViewCount(questionId, userId);
        when(questionViewCountService.getViewCount(questionId, userId)).thenReturn(1L);

        QuestionResponseDto response = questionCrudService.readQuestion(questionId, userId);

        assertNotNull(response);
        assertEquals(question.getId(), response.getQuestionId());
        assertEquals(question.getContent(), response.getContent());
        assertEquals(question.getTitle(), response.getTitle());
        assertEquals(1L, response.getViewCount());
        assertEquals(stringTags, response.getTags());
    }

//    @Test
//    void testReadQuestionsByPage_success() {
//        int page = 0;
//        int size = 10;
//        List<Question> questions = List.of(
//                Question.builder().id(1L).title("title1").build(),
//                Question.builder().id(2L).title("title2").build()
//        );
//        when(questionRepository.getQuestionByPage(page, size)).thenReturn(questions);
//        when(tagService.getStringTagsByQuestionId(anyLong())).thenReturn(
//                List.of("tag1", "tag2")
//        );
//        when(questionRepository.count()).thenReturn((long) questions.size());
//
//        Page<QuestionPageResponseDto> response = questionCrudService.readQuestionsByPage(page, size);
//
//        assertNotNull(response);
//        assertEquals(questions.get(0).getId(), response.getContent().get(0).getQuestionId());
//        assertEquals(questions.get(0).getTitle(), response.getContent().get(0).getTitle());
//        assertEquals(List.of("tag1", "tag2"), response.getContent().get(0).getTags());
//    }

}
