package com.forum.project.domain.tag.service;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.question.entity.QuestionTag;
import com.forum.project.domain.tag.repository.QuestionTagRepository;
import com.forum.project.domain.tag.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionTagService {
    private final QuestionTagRepository questionTagRepository;

    private List<QuestionTag> buildQuestionTags(Long questionId, List<Tag> tags) {
        return tags.stream()
                .map(tag -> QuestionTag.builder()
                        .questionId(questionId)
                        .tagId(tag.getId())
                        .build())
                .toList();
    }

    private void validateTags(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_REQUEST,
                    "Tags must not be empty.");
        }
    }

    public List<QuestionTag> saveQuestionTag(Long questionId, List<Tag> tags) {
        validateTags(tags);
        List<QuestionTag> questionTags = buildQuestionTags(questionId, tags);
        if (Arrays.stream(questionTagRepository.saveAll(questionTags)).anyMatch(r -> r ==0))
            throw new ApplicationException(ErrorCode.DATABASE_ERROR);

        return questionTags;
    }

    public List<Long> getTagIdsByQuestionId(Long questionId) {
        return questionTagRepository.findTagIdsByQuestionId(questionId);
    }

    public List<Long> getQuestionIdsByTagId(Long tagId) {
        return questionTagRepository.findQuestionIdsByTagId(tagId);
    }
}
