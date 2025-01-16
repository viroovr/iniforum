package com.forum.project.domain.bookmark;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository {

    Optional<Bookmark> findByUserIdAndQuestionId(Long userId, Long questionId);

    Bookmark save(Bookmark bookmark);

    void delete(Bookmark bookmark);

    List<Bookmark> findAllByUserId(Long userId);
}
