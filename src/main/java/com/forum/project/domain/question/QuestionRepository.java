package com.forum.project.domain.question;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository {
    Optional<Question> findById(Long id);
    Question save(Question question);
    void deleteById(Long id);
    long count();
    List<Question> getQuestionByPage(int page, int size);

    List<Question> findQuestionsByKeyword(String keyword, int page, int size);
    void updateViewCount(Long questionId, Long viewCount);

    List<Question> searchQuestionsByUser(Long id, int page, int size);

    Long getViewCountByQuestionId(Long questionId);

    List<Question> searchQuestionsByQuestionIds(List<Long> questionIds, int page, int size);

    List<Question> findQuestionsByStatus(String status, int page, int size);

    boolean existsById(Long questionId);

    long countUnansweredQuestions();

    long countByKeyword(String keyword);

    Long countByUserId(Long userId);

    Long countByQuestionIds(List<Long> questionIds);

    Question update(Question question);

    List<Question> findAll(Pageable pageable);

    Long countByStatus(String status);
}
