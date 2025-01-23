package com.forum.project.application.question;

import com.forum.project.domain.bookmark.Bookmark;
import com.forum.project.domain.bookmark.BookmarkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        when(bookmarkRepository.existsByUserIdAndQuestionId(userId, questionId))
                .thenReturn(false);
        when(bookmarkRepository.insert(any(Bookmark.class))).thenReturn(any(Bookmark.class));

        questionBookmarkService.saveQuestionBookmark(questionId, userId);

        verify(questionBookmarkService).saveQuestionBookmark(questionId, userId);
    }
}