package com.forum.project.presentation.question;

import com.forum.project.application.question.QuestionService;
import com.forum.project.presentation.question.dto.QuestionPageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/q")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

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
