package com.forum.project.presentation.question;

import com.forum.project.application.question.QuestionService;
import com.forum.project.application.jwt.TokenService;
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
    public ResponseEntity<QuestionResponseDto> postQuestion(
            @RequestBody QuestionRequestDto questionRequestDto,
            @RequestHeader(value = "Authorization") String header
    ) {
        QuestionResponseDto questionResponseDto = questionService.createQuestion(questionRequestDto, header);
        return ResponseEntity.status(HttpStatus.CREATED).body(questionResponseDto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<QuestionResponseDto> getQuestionById(
            @PathVariable Long id
    ) {
        QuestionResponseDto questionResponseDto = questionService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(questionResponseDto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<QuestionResponseDto> updateQuestion(
            @PathVariable("id") Long questionId,
            @RequestBody QuestionRequestDto questionRequestDto,
            @RequestHeader(value = "Authorization") String header
    ) {
        QuestionResponseDto questionResponseDto = questionService.updateQuestion(questionId, questionRequestDto, header);
        return ResponseEntity.status(HttpStatus.OK).body(questionResponseDto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Map<String, String>> deleteQuestion(
            @PathVariable("id") Long questionId,
            @RequestHeader(value = "Authorization") String header
    ) {
        questionService.deleteQuestion(questionId, header);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Question deleted successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/questions")
    public Page<QuestionResponseDto> getQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return questionService.getQuestionsByPage(page, size);
        } else {
            return questionService.searchQuestions(keyword, page, size);
        }
    }
}
