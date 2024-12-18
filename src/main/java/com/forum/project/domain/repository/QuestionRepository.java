package com.forum.project.domain.repository;

import com.forum.project.domain.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository {
    Optional<Question> findById(Long id);
    Question save(Question question);
    void deleteById(Long id);
    long count();
    List<Question> getQuestionByPage(int page, int size);

    List<Question> searchQuestions(String keyword, int page, int size);
    void updateViewCount(Long questionId, Integer viewCount);

    Long getTotalUserQuestionCount(Long id);

    List<Question> searchQuestionsByUser(Long id, int page, int size);
}
