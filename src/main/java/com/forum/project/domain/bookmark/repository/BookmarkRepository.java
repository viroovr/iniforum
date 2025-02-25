package com.forum.project.domain.bookmark.repository;

import com.forum.project.domain.bookmark.dto.BookmarkRequestDto;
import com.forum.project.domain.bookmark.entity.Bookmark;
import com.forum.project.domain.bookmark.vo.BookmarkKey;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository {

    Optional<Bookmark> findByUserIdAndQuestionId(Long userId, Long questionId);

    Optional<BookmarkKey> insertAndReturnGeneratedKeys(BookmarkRequestDto bookmarkRequestDto);

    int delete(Long userId, Long questionId);

    List<Bookmark> findAllByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndQuestionId(Long userId, Long questionId);
}
