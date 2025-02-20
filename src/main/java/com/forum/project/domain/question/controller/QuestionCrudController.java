package com.forum.project.domain.question.controller;

import com.forum.project.domain.question.service.QuestionCrudService;
import com.forum.project.domain.question.mapper.QuestionDtoFactory;
import com.forum.project.domain.auth.service.AuthorizationService;
import com.forum.project.domain.user.entity.User;
import com.forum.project.core.base.BaseResponseDto;
import com.forum.project.domain.question.dto.QuestionCreateDto;
import com.forum.project.domain.question.dto.QuestionRequestDto;
import com.forum.project.domain.question.dto.QuestionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/questions")
@RequiredArgsConstructor
public class QuestionCrudController {
    private final QuestionCrudService questionCrudService;
    private final AuthorizationService authorizationService;

    @PostMapping(value = "/post")
    public ResponseEntity<QuestionResponseDto> postQuestion(
            @RequestBody QuestionRequestDto questionRequestDto,
            @RequestHeader(value = "Authorization") String header
    ) {
        User user = authorizationService.extractUserByHeader(header);
        QuestionCreateDto questionCreateDto = QuestionDtoFactory.toCreateDto(questionRequestDto, user);
        QuestionResponseDto response = questionCrudService.create(questionCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<QuestionResponseDto> readQuestion(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization") String header
    ) {
        Long userId = authorizationService.extractUserId(header);
        QuestionResponseDto questionResponseDto = questionCrudService.readQuestion(id, userId);
        return ResponseEntity.status(HttpStatus.OK).body(questionResponseDto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<QuestionResponseDto> updateQuestion(
            @PathVariable("id") Long questionId,
            @RequestBody QuestionRequestDto dto,
            @RequestHeader(value = "Authorization") String header
    ) {
        Long userId = authorizationService.extractUserId(header);
        QuestionResponseDto questionResponseDto = questionCrudService.updateTitleAndContent(
                questionId, userId, dto.getTitle(), dto.getContent());
        return ResponseEntity.status(HttpStatus.OK).body(questionResponseDto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<BaseResponseDto> deleteQuestion(
            @PathVariable("id") Long questionId,
            @RequestHeader(value = "Authorization") String header
    ) {
        Long userId = authorizationService.extractUserId(header);
        questionCrudService.delete(questionId, userId);

        BaseResponseDto response = new BaseResponseDto("Question deleted successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
