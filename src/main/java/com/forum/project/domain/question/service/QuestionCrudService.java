package com.forum.project.domain.question.service;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.tag.service.TagService;
import com.forum.project.domain.like.vo.LikeStatus;
import com.forum.project.domain.question.entity.Question;
import com.forum.project.domain.question.mapper.QuestionDtoFactory;
import com.forum.project.domain.question.repository.QuestionRepository;
import com.forum.project.domain.question.dto.QuestionCreateDto;
import com.forum.project.domain.question.dto.QuestionResponseDto;
import com.forum.project.domain.question.validator.QuestionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionCrudService {

    private final QuestionRepository questionRepository;
    private final TagService tagService;
    private final QuestionValidator questionValidator;
    private final QuestionViewCountService questionViewCountService;

    @Transactional
    public QuestionResponseDto create(QuestionCreateDto dto) {
        Question requestQuestion = QuestionDtoFactory.toEntity(dto);
        Map<String, Object> keys = questionRepository.insertAndReturnGeneratedKeys(requestQuestion);

        requestQuestion.setKeys(keys);
        List<String> stringTags = tagService.createAndAttachTagsToQuestion(
                dto.getTagRequestDto(), requestQuestion.getId());
        return QuestionDtoFactory.toResponseDto(requestQuestion, stringTags, 0L);
    }

    private Question findQuestionByIdOrThrow(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.QUESTION_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public QuestionResponseDto readQuestion(Long questionId, Long userId) {
        Question question = findQuestionByIdOrThrow(questionId);
        List<String> tags = tagService.getStringTagsByQuestionId(questionId);
        questionViewCountService.incrementViewCount(questionId, userId);
        Long viewCount = questionViewCountService.getViewCount(questionId, userId);
        return QuestionDtoFactory.toResponseDto(question, tags, viewCount);
    }

    @Transactional
    public QuestionResponseDto updateTitleAndContent(Long id, Long userId, String title, String content) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.QUESTION_NOT_FOUND));

        questionValidator.validateOwnership(userId, question.getUserId());
        questionRepository.updateTitleAndContent(id, title, content);

        question.setTitle(title);
        question.setContent(content);
        return QuestionDtoFactory.toResponseDto(question);
    }

    @Transactional
    public void incrementVotedCount(Long questionId, LikeStatus likeStatus) {
        Question question = findQuestionByIdOrThrow(questionId);

        switch (likeStatus) {
            case LIKE:
                question.incrementUpVotedCount();
                questionRepository.updateUpVotedCount(questionId, question.getUpVotedCount());
                break;
            case DISLIKE:
                question.incrementDownVotedCount();
                questionRepository.updateDownVotedCount(questionId, question.getUpVotedCount());
                break;
            case NONE:
                break;
            default:
                throw new ApplicationException(ErrorCode.INVALID_REQUEST, "Unsupported LikeStatus: " + likeStatus);
        }
    }

    public void decrementVotedCount(Long questionId, LikeStatus likeStatus) {
        Question question = findQuestionByIdOrThrow(questionId);

        switch (likeStatus) {
            case LIKE:
                question.decrementUpVotedCount();
                questionRepository.updateUpVotedCount(questionId, question.getUpVotedCount());
                break;
            case DISLIKE:
                question.decrementDownVotedCount();
                questionRepository.updateDownVotedCount(questionId, question.getDownVotedCount());
                break;
            case NONE:
                break;
            default:
                throw new ApplicationException(ErrorCode.INVALID_REQUEST, "Unsupported LikeStatus: " + likeStatus);
        }

    }

    @Transactional
    public void delete(Long questionId, Long userId) {
        Question question = findQuestionByIdOrThrow(questionId);

        questionValidator.validateOwnership(userId, question.getUserId());

        questionRepository.deleteById(questionId);
    }
}
