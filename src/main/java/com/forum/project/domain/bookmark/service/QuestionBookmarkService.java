package com.forum.project.domain.bookmark.service;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.bookmark.entity.Bookmark;
import com.forum.project.domain.bookmark.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionBookmarkService {
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public void saveQuestionBookmark(Long questionId, Long userId) {
        if (bookmarkRepository.existsByUserIdAndQuestionId(userId, questionId))
            throw new ApplicationException(ErrorCode.BOOKMARK_NOT_FOUND);

        Bookmark bookmark = Bookmark.builder()
                .userId(userId)
                .questionId(questionId)
                .build();

        bookmark.setKeys(bookmarkRepository.insertAndReturnGeneratedKeys(bookmark));
    }

    @Transactional
    public void removeBookmark(Long questionId, Long userId) {
        Bookmark bookmark = bookmarkRepository.findByUserIdAndQuestionId(userId, questionId)
                .orElseThrow(() -> new IllegalArgumentException("Bookmark not found for this question."));

        bookmarkRepository.delete(userId, questionId);
    }

    @Transactional(readOnly = true)
    public List<Bookmark> getUserBookmarks(Long userId) {
        return bookmarkRepository.findAllByUserId(userId);
    }
}
