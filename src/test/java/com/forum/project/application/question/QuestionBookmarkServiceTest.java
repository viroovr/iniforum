package com.forum.project.application.question;

import com.forum.project.domain.bookmark.entity.Bookmark;
import com.forum.project.domain.bookmark.service.QuestionBookmarkService;
import com.forum.project.infrastructure.persistence.key.BookmarkKey;
import com.forum.project.domain.bookmark.repository.BookmarkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionBookmarkServiceTest {
    @InjectMocks
    private QuestionBookmarkService questionBookmarkService;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Test
    void testSaveQuestionBookmark_success() {
        Long questionId = 1L;
        Long userId = 1L;

        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        Map<String, Object> generatedKeys = new HashMap<>();
        generatedKeys.put(BookmarkKey.ID, 1L);
        generatedKeys.put(BookmarkKey.CREATED_DATE, timestamp);
        generatedKeys.put(BookmarkKey.LAST_ACCESSED_DATE, timestamp);

        when(bookmarkRepository.existsByUserIdAndQuestionId(userId, questionId))
                .thenReturn(false);
        when(bookmarkRepository.insertAndReturnGeneratedKeys(any(Bookmark.class))).thenReturn(generatedKeys);

        questionBookmarkService.saveQuestionBookmark(questionId, userId);
    }
}