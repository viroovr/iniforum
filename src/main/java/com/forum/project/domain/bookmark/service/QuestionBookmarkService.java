package com.forum.project.domain.bookmark.service;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.bookmark.dto.BookmarkRequestDto;
import com.forum.project.domain.bookmark.entity.Bookmark;
import com.forum.project.domain.bookmark.mapper.BookmarkDtoMapper;
import com.forum.project.domain.bookmark.repository.BookmarkRepository;
import com.forum.project.domain.bookmark.vo.BookmarkKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionBookmarkService {
    private final BookmarkRepository bookmarkRepository;

    private BookmarkKey insertAndReturnKeys(BookmarkRequestDto dto) {
        return bookmarkRepository.insertAndReturnGeneratedKeys(dto)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DATABASE_ERROR,
                                "Bookmark 생성 이후, 키 반환 오류"));
    }

    @Transactional
    public Bookmark saveQuestionBookmark(BookmarkRequestDto dto) {
        if (bookmarkRepository.existsByUserIdAndQuestionId(dto.getUserId(), dto.getQuestionId()))
            throw new ApplicationException(ErrorCode.BOOKMARK_ALREADY_EXISTS);

        return BookmarkDtoMapper.toEntity(dto, insertAndReturnKeys(dto));
    }

    @Transactional
    public void removeBookmark(Long questionId, Long userId) {
        if (bookmarkRepository.delete(userId, questionId) == 0)
            throw new ApplicationException(ErrorCode.DATABASE_ERROR, "Bookmark 삭제 실패");
    }

    @Transactional(readOnly = true)
    public List<Bookmark> getUserBookmarks(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookmarkRepository.findAllByUserId(userId, pageable);
    }
}
