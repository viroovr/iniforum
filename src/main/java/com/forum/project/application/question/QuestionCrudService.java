package com.forum.project.application.question;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.application.tag.TagService;
import com.forum.project.domain.like.LikeStatus;
import com.forum.project.domain.question.Question;
import com.forum.project.domain.question.QuestionRepository;
import com.forum.project.presentation.question.dto.QuestionCreateDto;
import com.forum.project.presentation.question.dto.QuestionResponseDto;
import com.forum.project.presentation.question.dto.QuestionUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Question savedQuestion = questionRepository.save(requestQuestion);

        List<String> stringTags = tagService.createAndAttachTagsToQuestion(
                dto.getTagRequestDto(), savedQuestion.getId());
        return QuestionDtoFactory.toResponseDto(savedQuestion, stringTags, 0L);
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
    public QuestionResponseDto update(QuestionUpdateDto dto) {
        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.QUESTION_NOT_FOUND));

        questionValidator.validateOwnership(dto.getUserId(), question.getUserId());

        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());

        Question updatedQuestion = questionRepository.update(question);

        List<String> updatedTags = tagService.getStringUpdateTags(dto.getTagRequestDto());
        Long viewCount = questionViewCountService.getViewCount(question.getId(), question.getUserId());
        return QuestionDtoFactory.toResponseDto(updatedQuestion, updatedTags, viewCount);
    }

    @Transactional
    public void updateUpVotedCount(Long questionId, LikeStatus likeStatus) {
        Question question = findQuestionByIdOrThrow(questionId);

        switch (likeStatus) {
            case LIKE:
                question.incrementUpVotedCount();
                break;
            case DISLIKE:
                question.incrementDownVotedCount();
                break;
            case NONE:
                break;
            default:
                throw new ApplicationException(ErrorCode.INVALID_REQUEST, "Unsupported LikeStatus: " + likeStatus);
        }

       questionRepository.update(question);
    }

    public void decrementVotedCount(Long questionId, LikeStatus likeStatus) {
        Question question = findQuestionByIdOrThrow(questionId);

        switch (likeStatus) {
            case LIKE:
                question.decrementUpVotedCount();
                break;
            case DISLIKE:
                question.decrementDownVotedCount();
                break;
            case NONE:
                break;
            default:
                throw new ApplicationException(ErrorCode.INVALID_REQUEST, "Unsupported LikeStatus: " + likeStatus);
        }

        questionRepository.update(question);
    }

    @Transactional
    public void delete(Long questionId, Long userId) {
        Question question = findQuestionByIdOrThrow(questionId);

        questionValidator.validateOwnership(userId, question.getUserId());

        questionRepository.deleteById(questionId);
    }
}
