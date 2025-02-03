package com.forum.project.domain.question;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QuestionRepository {
    Optional<Question> findById(Long id);
    List<Question> findByUserId(Long userId, int page, int size);
    List<Question> findQuestionByIds(List<Long> questionIds);
    List<Question> findByStatus(String status, int page, int size);

    Map<String, Object> insertAndReturnGeneratedKeys(Question question);

    void deleteById(Long id);

    List<Question> getByPage(int page, int size);
    Long getViewCountById(Long questionId);

    List<Question> searchByTitle(String keyword, int page, int size);
    List<Question> searchByContent(String keyword, int page, int size);
    List<Question> searchByTitleOrContent(String keyword, int page, int size);

    void updateViewCount(Long id, Long plus);
    void updateTitleAndContent(Long id, String title, String content);
    void updateUpVotedCount(Long questionId, Long delta);
    void updateDownVotedCount(Long questionId, Long delta);

    Long countAll();
    Long countByStatus(String status);
    Long countByTitleKeyword(String keyword);
    Long countByContentKeyword(String keyword);
    Long countByContentOrTitleKeyword(String keyword);
    Long countByUserId(Long userId);
    Long countByQuestionIds(List<Long> questionIds);

    boolean existsById(Long id);


    List<Question> getByPageable(Pageable pageable);

}
