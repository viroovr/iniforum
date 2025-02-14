package com.forum.project.domain.question.controller;

import com.forum.project.domain.bookmark.service.QuestionBookmarkService;
import com.forum.project.domain.auth.service.AuthenticationService;
import com.forum.project.domain.bookmark.entity.Bookmark;
import com.forum.project.core.base.BaseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/questions/bookmarks")
@RequiredArgsConstructor
public class QuestionBookmarkController {
    private final QuestionBookmarkService questionBookmarkService;
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/{id}")
    public ResponseEntity<BaseResponseDto> saveQuestionBookmark(
            @PathVariable(value = "id") Long questionId,
            @RequestHeader(value = "Authorization") String header
    ) {
        Long userId = authenticationService.extractUserId(header);
        questionBookmarkService.saveQuestionBookmark(questionId, userId);
        BaseResponseDto response = new BaseResponseDto("Bookmark successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<BaseResponseDto> deleteQuestionBookmark(
            @PathVariable(value = "id") Long questionId,
            @RequestHeader(value = "Authorization") String header
    ) {
        Long userId = authenticationService.extractUserId(header);
        questionBookmarkService.removeBookmark(questionId, userId);
        BaseResponseDto response = new BaseResponseDto("Delete Bookmark successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<Bookmark>> getQuestionBookmarks(
            @RequestHeader(value = "Authorization") String header
    ) {
        Long userId = authenticationService.extractUserId(header);
        List<Bookmark> response = questionBookmarkService.getUserBookmarks(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}