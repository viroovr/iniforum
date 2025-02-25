package com.forum.project.application.question;

import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.bookmark.dto.BookmarkRequestDto;
import com.forum.project.domain.bookmark.entity.Bookmark;
import com.forum.project.domain.bookmark.repository.BookmarkRepository;
import com.forum.project.domain.bookmark.service.QuestionBookmarkService;
import com.forum.project.domain.bookmark.vo.BookmarkKey;
import com.forum.project.presentation.dtos.TestDtoFactory;
import com.forum.project.testUtils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionBookmarkServiceTest {
    @InjectMocks
    private QuestionBookmarkService questionBookmarkService;

    @Mock
    private BookmarkRepository bookmarkRepository;

    private BookmarkRequestDto bookmarkRequestDto;

    @BeforeEach
    void setUp() {
        bookmarkRequestDto = TestDtoFactory.createBookmarkRequestDto();
    }

    @Test
    void saveQuestionBookmark() {
        when(bookmarkRepository.existsByUserIdAndQuestionId(anyLong(), anyLong())).thenReturn(false);
        when(bookmarkRepository.insertAndReturnGeneratedKeys(any(BookmarkRequestDto.class)))
                .thenReturn(Optional.of(mock(BookmarkKey.class)));

        assertThat(questionBookmarkService.saveQuestionBookmark(bookmarkRequestDto))
                .extracting(Bookmark::getQuestionId)
                .isEqualTo(bookmarkRequestDto.getQuestionId());
    }

    @Test
    void saveQuestionBookmark_alreadyExists() {
        when(bookmarkRepository.existsByUserIdAndQuestionId(anyLong(), anyLong())).thenReturn(true);

        TestUtils.assertApplicationException(
                () -> questionBookmarkService.saveQuestionBookmark(bookmarkRequestDto),
                ErrorCode.BOOKMARK_ALREADY_EXISTS
        );
    }

    @Test
    void saveQuestionBookmark_keyReturningError() {
        when(bookmarkRepository.existsByUserIdAndQuestionId(anyLong(), anyLong())).thenReturn(false);
        when(bookmarkRepository.insertAndReturnGeneratedKeys(any(BookmarkRequestDto.class)))
                .thenReturn(Optional.empty());

        TestUtils.assertApplicationException(
                () -> questionBookmarkService.saveQuestionBookmark(bookmarkRequestDto),
                ErrorCode.DATABASE_ERROR
        );
    }

    @Test
    void removeBookmark() {
        when(bookmarkRepository.delete(anyLong(), anyLong())).thenReturn(1);

        assertThatNoException().isThrownBy(() -> questionBookmarkService.removeBookmark(1L,1L));
    }

    @Test
    void removeBookmark_removeFail() {
        when(bookmarkRepository.delete(anyLong(), anyLong())).thenReturn(0);

        TestUtils.assertApplicationException(
                () -> questionBookmarkService.removeBookmark(1L,1L),
                ErrorCode.DATABASE_ERROR
        );
    }

    @Test
    void getUserBookmarks() {
        when(bookmarkRepository.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(Collections.emptyList());

        assertThat(questionBookmarkService.getUserBookmarks(1L, 0, 10)).isEmpty();
    }
}