package com.forum.project.domain.question.controller;

import com.forum.project.domain.question.service.QuestionQueryService;
import com.forum.project.domain.question.vo.QuestionSortType;
import com.forum.project.domain.auth.service.AuthenticationService;
import com.forum.project.domain.question.vo.QuestionStatus;
import com.forum.project.domain.question.dto.QuestionPageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/questions/query")
@RequiredArgsConstructor
public class QuestionQueryController {
    private final AuthenticationService authenticationService;
    private final QuestionQueryService questionQueryService;

    @GetMapping
    public ResponseEntity<Page<QuestionPageResponseDto>> getQuestionsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<QuestionPageResponseDto> response = questionQueryService.readQuestionsByPage(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/sorted")
    public ResponseEntity<Page<QuestionPageResponseDto>> getQuestionsSortedBy(
            @RequestParam String sortType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        QuestionSortType questionSortType = QuestionSortType.fromString(sortType);
        Page<QuestionPageResponseDto> response =
                questionQueryService.readQuestionsSortedBy(questionSortType, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<QuestionPageResponseDto>> getQuestionsByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<QuestionPageResponseDto> response = questionQueryService.readQuestionsByKeyword(keyword, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Page<QuestionPageResponseDto>> getQuestionsByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        QuestionStatus questionStatus = QuestionStatus.fromString(status);
        Page<QuestionPageResponseDto> response = questionQueryService.readQuestionsByStatus(questionStatus, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<QuestionPageResponseDto>> getQuestionsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<QuestionPageResponseDto> response = questionQueryService.readQuestionsByUserId(userId, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/tags/{tag}")
    public ResponseEntity<Page<QuestionPageResponseDto>> getQuestionsByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<QuestionPageResponseDto> response = questionQueryService.readQuestionsByTag(tag, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
