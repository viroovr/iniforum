package com.forum.project.domain.bookmark.controller;

import com.forum.project.core.common.TokenUtil;
import com.forum.project.domain.auth.service.TokenService;
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
@RequestMapping(value = "/api/v1/questions/bookmarks")
@RequiredArgsConstructor
public class QuestionBookmarkController {
    private final QuestionBookmarkService questionBookmarkService;
    private final TokenService tokenService;

    private Long getUserId(String header) {
        return tokenService.getUserId(TokenUtil.extractToken(header));
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<BaseResponseDto> saveQuestionBookmark(
            @PathVariable(value = "id") Long questionId,
            @RequestHeader(value = "Authorization") String header
    ) {
        questionBookmarkService.saveQuestionBookmark(questionId, getUserId(header));
        return BaseResponseDto.buildResponse("Create Bookmark successfully", HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<BaseResponseDto> deleteQuestionBookmark(
            @PathVariable(value = "id") Long questionId,
            @RequestHeader(value = "Authorization") String header
    ) {
        questionBookmarkService.removeBookmark(questionId, getUserId(header));
        return BaseResponseDto.buildOkResponse("Delete Bookmark successfully");
    }

    @GetMapping
    public ResponseEntity<List<Bookmark>> getQuestionBookmarks(
            @RequestHeader(value = "Authorization") String header,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<Bookmark> response = questionBookmarkService.getUserBookmarks(getUserId(header), page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}