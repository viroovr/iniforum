package com.forum.project.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository {
    Question findById(Long id);
    Question save(Question question);
    void deleteById(Long id);
    long count();
    List<Question> getQuestionByPage(int page, int size);

    Page<Question> searchQuestions(@Param("keyword") String keyword, Pageable pageable);
}
