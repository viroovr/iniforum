package com.forum.project.domain.question.tag;

import java.util.List;
import java.util.Optional;

public interface QuestionTagRepository {
    int insert(QuestionTag questionTag);
    int[]  saveAll(List<QuestionTag> questionTags);

    Optional<QuestionTag> findByQuestionIdAndTagId(Long questionId, Long tagId);
    List<Long> findTagIdsByQuestionId(Long questionId);
    List<Long> findQuestionIdsByTagId(Long tagId);

    void deleteByQuestionId(Long questionId);
    void deleteByTagId(Long tagId);
}
