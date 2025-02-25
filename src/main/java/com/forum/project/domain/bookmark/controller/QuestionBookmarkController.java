package com.forum.project.domain.bookmark.controller;

import com.forum.project.core.common.TokenUtil;
import com.forum.project.domain.auth.aspect.ExtractUserId;
import com.forum.project.domain.auth.service.TokenService;
import com.forum.project.domain.bookmark.dto.BookmarkRequestDto;
import com.forum.project.domain.bookmark.service.QuestionBookmarkService;
import com.forum.project.domain.bookmark.entity.Bookmark;
import com.forum.project.core.base.BaseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/questions/bookmarks")
public class QuestionBookmarkController {
    private final QuestionBookmarkService questionBookmarkService;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<Bookmark> saveQuestionBookmark(
            @RequestBody BookmarkRequestDto dto,
            @ExtractUserId Long userId
    ) {
        dto.setUserId(userId);
        Bookmark result = questionBookmarkService.saveQuestionBookmark(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @RequestMapping(value = "/{id}")
    public ResponseEntity<BaseResponseDto> deleteQuestionBookmark(
            @PathVariable(value = "id") Long questionId,
            @ExtractUserId Long userId
    ) {
        questionBookmarkService.removeBookmark(questionId, userId);
        return BaseResponseDto.buildOkResponse("Delete Bookmark successfully");
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Bookmark>> getQuestionBookmarks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ExtractUserId Long userId
    ) {
        List<Bookmark> response = questionBookmarkService.getUserBookmarks(userId, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}