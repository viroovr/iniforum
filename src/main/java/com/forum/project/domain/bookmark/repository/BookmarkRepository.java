package com.forum.project.domain.bookmark.repository;

import com.forum.project.domain.bookmark.entity.Bookmark;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookmarkRepository {

    Optional<Bookmark> findByUserIdAndQuestionId(Long userId, Long questionId);

    Map<String, Object> insertAndReturnGeneratedKeys(Bookmark bookmark);

    void delete(Long userId, Long questionId);

    List<Bookmark> findAllByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndQuestionId(Long userId, Long questionId);
}
