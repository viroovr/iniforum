package com.forum.project.presentation.controller;

import com.forum.project.application.question.QuestionService;
import com.forum.project.application.security.jwt.TokenService;
import com.forum.project.presentation.dtos.question.RequestQuestionDto;
import com.forum.project.presentation.dtos.question.ResponseQuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/q")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    private final TokenService tokenService;

    @PostMapping(value = "/post")
    public ResponseEntity<ResponseQuestionDto> postQuestion(
            @RequestBody RequestQuestionDto requestQuestionDto,
            @RequestHeader(value = "Authorization") String header
    ) {
        String accessToken = tokenService.extractTokenByHeader(header);
        ResponseQuestionDto responseQuestionDto = questionService.createPost(requestQuestionDto, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseQuestionDto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseQuestionDto> getQuestionById(
            @PathVariable Long id
    ) {
        ResponseQuestionDto responseQuestionDto = questionService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(responseQuestionDto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ResponseQuestionDto> updateQuestion(
            @PathVariable("id") Long questionId,
            @RequestBody RequestQuestionDto requestQuestionDto,
            @RequestHeader(value = "Authorization") String header
            ) {
        String accessToken = tokenService.extractTokenByHeader(header);
        ResponseQuestionDto responseQuestionDto = questionService.updateQuestion(questionId, requestQuestionDto, accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(responseQuestionDto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Map<String, String>> deleteQuestion(
            @PathVariable("id") Long questionId,
            @RequestHeader(value = "Authorization") String header
    ) {
        String accessToken = tokenService.extractTokenByHeader(header);
        questionService.deleteQuestion(questionId, accessToken);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Question deleted successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/questions")
    public Page<ResponseQuestionDto> getQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return questionService.getQuestionsByPage(page, size);
        } else {
            return questionService.searchPosts(keyword, page, size);
        }
    }
}
