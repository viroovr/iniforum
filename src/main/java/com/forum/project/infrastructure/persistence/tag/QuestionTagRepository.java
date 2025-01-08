package com.forum.project.infrastructure.persistence.tag;

import com.forum.project.domain.tag.QuestionTag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionTagRepository {
    void save(QuestionTag questionTag);

    List<Long> findTagIdsByQuestionId(Long questionId);

    List<Long> findQuestionIdsByTagId(Long tagId);

    void deleteByQuestionId(Long questionId);

    void deleteByTagId(Long tagId);
}
