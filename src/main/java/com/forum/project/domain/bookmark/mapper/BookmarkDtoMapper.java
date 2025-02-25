package com.forum.project.domain.bookmark.mapper;

import com.forum.project.domain.bookmark.dto.BookmarkRequestDto;
import com.forum.project.domain.bookmark.entity.Bookmark;
import com.forum.project.domain.bookmark.vo.BookmarkKey;

public class BookmarkDtoMapper {
    public static Bookmark toEntity(BookmarkRequestDto dto, BookmarkKey bookmarkKey) {
        Bookmark bookmark =  Bookmark.builder()
                .questionId(dto.getQuestionId())
                .userId(dto.getUserId())
                .notes(dto.getNotes())
                .build();
        bookmark.setKeys(bookmarkKey);
        return bookmark;
    }
}
