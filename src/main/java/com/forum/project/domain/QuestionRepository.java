package com.forum.project.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findAllByOrderByCreatedDateDesc(Pageable pageable);

    @Query("SELECT p FROM Question p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
        "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Question> searchQuestions(@Param("keyword") String keyword, Pageable pageable);
}
