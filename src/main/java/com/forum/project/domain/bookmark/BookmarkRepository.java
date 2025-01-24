package com.forum.project.domain.bookmark;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookmarkRepository {

    Optional<Bookmark> findByUserIdAndQuestionId(Long userId, Long questionId);

    Map<String, Object> insertAndReturnGeneratedKeys(Bookmark bookmark);

    void delete(Long userId, Long questionId);

    List<Bookmark> findAllByUserId(Long userId);

    boolean existsByUserIdAndQuestionId(Long userId, Long questionId);
}
