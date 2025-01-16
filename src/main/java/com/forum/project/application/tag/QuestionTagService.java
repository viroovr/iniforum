package com.forum.project.application.tag;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.tag.QuestionTag;
import com.forum.project.domain.tag.QuestionTagRepository;
import com.forum.project.domain.tag.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionTagService {
    private final QuestionTagRepository questionTagRepository;

    private List<QuestionTag> buildQuestionTags(Long questionId, List<Tag> tags) {
        return tags.stream()
                .map(tag -> new QuestionTag(questionId, tag.getId()))
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

        return questionTagRepository.saveAll(questionTags);
    }

    public List<Long> getTagIdsByQuestionId(Long questionId) {
        return questionTagRepository.findTagIdsByQuestionId(questionId);
    }

    public List<Long> getQuestionIdsByTagId(Long tagId) {
        return questionTagRepository.findQuestionIdsByTagId(tagId);
    }


}
