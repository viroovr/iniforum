package com.forum.project.application.bookmark;

import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.domain.bookmark.Bookmark;
import com.forum.project.domain.bookmark.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionBookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final AuthenticationService authenticationService;

    @Transactional
    public void bookmarkQuestion(Long questionId, Long userId) {
        bookmarkRepository.findByUserIdAndQuestionId(userId, questionId).ifPresent(bookmark -> {
            throw new IllegalArgumentException("This question is already bookmarked.");
        });

        Bookmark bookmark = Bookmark.builder()
                .userId(userId)
                .questionId(questionId)
                .build();

        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void removeBookmark(Long questionId, String header) {
        Long userId = authenticationService.extractUserId(header);

        Bookmark bookmark = bookmarkRepository.findByUserIdAndQuestionId(userId, questionId)
                .orElseThrow(() -> new IllegalArgumentException("Bookmark not found for this question."));

        bookmarkRepository.delete(bookmark);
    }

    @Transactional(readOnly = true)
    public List<Bookmark> getUserBookmarks(String header) {
        Long userId = authenticationService.extractUserId(header);

        return bookmarkRepository.findAllByUserId(userId);
    }
}
