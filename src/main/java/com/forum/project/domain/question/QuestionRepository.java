package com.forum.project.domain.question;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QuestionRepository {
    Map<String, Object> insertAndReturnGeneratedKeys(Question question);

    Optional<Question> findById(Long id);
    List<Question> findByUserId(Long userId, int page, int size);
    List<Question> findQuestionByIds(List<Long> questionIds);
    List<Question> findByStatus(String status, int page, int size);

    List<Question> getByPage(int page, int size);
    List<Question> getByPageable(Pageable pageable);
    Long getViewCountById(Long id);

    List<Question> searchByTitle(String keyword, int page, int size);
    List<Question> searchByContent(String keyword, int page, int size);
    List<Question> searchByTitleOrContent(String keyword, int page, int size);

    Long countAll();
    Long countByStatus(String status);
    Long countByTitleKeyword(String keyword);
    Long countByContentKeyword(String keyword);
    Long countByContentOrTitleKeyword(String keyword);
    Long countByUserId(Long userId);
    Long countByQuestionIds(List<Long> questionIds);

    boolean existsById(Long id);

    int updateViewCount(Long id, Long plus);
    int updateTitleAndContent(Long id, String title, String content);
    int updateUpVotedCount(Long questionId, Long delta);
    int updateDownVotedCount(Long questionId, Long delta);

    void deleteById(Long id);
}
