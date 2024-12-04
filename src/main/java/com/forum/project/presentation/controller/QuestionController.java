package com.forum.project.presentation.controller;

import com.forum.project.application.QuestionService;
import com.forum.project.domain.entity.Question;
import com.forum.project.presentation.dtos.question.RequestQuestionDto;
import com.forum.project.presentation.dtos.question.ResponseQuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/q")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<Question> postQuestion(
            @RequestBody RequestQuestionDto requestQuestionDto,
            @RequestHeader(value = "Authorization") String token
    ) {
        String jwt = extractToken(token);
        Question question = questionService.createPost(requestQuestionDto, jwt);
        return ResponseEntity.status(HttpStatus.CREATED).body(question);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ResponseQuestionDto> getQuestionById(
            @PathVariable Long id
    ) {
        ResponseQuestionDto responseQuestionDto = questionService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(responseQuestionDto);
    }

    @RequestMapping(value = "/questions", method = RequestMethod.GET)
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

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteQuestion(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization") String token
    ) {
        String jwt = extractToken(token);
        questionService.deleteQuestion(id, jwt);
        return ResponseEntity.ok("Question deleted successfully.");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ResponseQuestionDto> updateQuestion(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable Long id,
            @RequestBody RequestQuestionDto requestQuestionDto
    ) {
        String jwt = extractToken(token);
        ResponseQuestionDto responseQuestionDto = questionService.updateQuestion(id, requestQuestionDto, jwt);
        return ResponseEntity.ok(responseQuestionDto);
    }
    
    private String extractToken(String authorizationHeader) {
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }
}
