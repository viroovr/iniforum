package com.forum.project.presentation.question;

import com.forum.project.application.question.QuestionService;
import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.domain.user.User;
import com.forum.project.infrastructure.security.auth.ExtractUser;
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

    @PostMapping(value = "/post")
    public ResponseEntity<QuestionResponseDto> postQuestion(
            @RequestBody QuestionRequestDto questionRequestDto,
            @RequestHeader(value = "Authorization") String token
    ) {
        QuestionResponseDto questionResponseDto = questionService.createQuestion(questionRequestDto, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(questionResponseDto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<QuestionResponseDto> getQuestionById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization") String token
    ) {
        QuestionResponseDto questionResponseDto = questionService.readQuestion(id, token);
        return ResponseEntity.status(HttpStatus.OK).body(questionResponseDto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<QuestionResponseDto> updateQuestion(
            @PathVariable("id") Long questionId,
            @RequestBody QuestionRequestDto questionRequestDto,
            @RequestHeader(value = "Authorization") String token
    ) {
        QuestionResponseDto questionResponseDto = questionService.updateQuestion(questionId, questionRequestDto, token);
        return ResponseEntity.status(HttpStatus.OK).body(questionResponseDto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Map<String, String>> deleteQuestion(
            @PathVariable("id") Long questionId,
            @RequestHeader(value = "Authorization") String token
    ) {
        questionService.deleteQuestion(questionId, token);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Question deleted successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/questions")
    public Page<QuestionPageResponseDto> getQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return questionService.getQuestionsByPage(page, size);
        } else {
            return questionService.getQuestionsByKeyword(keyword, page, size);
        }
    }
}
