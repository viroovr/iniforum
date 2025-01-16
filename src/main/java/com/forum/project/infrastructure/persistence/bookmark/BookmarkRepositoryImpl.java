package com.forum.project.infrastructure.persistence.bookmark;

import com.forum.project.domain.bookmark.Bookmark;
import com.forum.project.domain.bookmark.BookmarkRepository;

import java.util.List;
import java.util.Optional;

public class BookmarkRepositoryImpl implements BookmarkRepository {
    @Override
    public Optional<Bookmark> findByUserIdAndQuestionId(Long userId, Long questionId) {
        return Optional.empty();
    }

    @Override
    public Bookmark save(Bookmark bookmark) {
        return null;
    }

    @Override
    public void delete(Bookmark bookmark) {

    }

    @Override
    public List<Bookmark> findAllByUserId(Long userId) {
        return List.of();
    }
}
