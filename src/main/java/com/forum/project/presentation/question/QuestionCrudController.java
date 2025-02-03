package com.forum.project.presentation.question;

import com.forum.project.application.question.QuestionCrudService;
import com.forum.project.application.question.QuestionDtoFactory;
import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.domain.user.User;
import com.forum.project.presentation.dtos.BaseResponseDto;
import com.forum.project.presentation.question.dto.QuestionCreateDto;
import com.forum.project.presentation.question.dto.QuestionRequestDto;
import com.forum.project.presentation.question.dto.QuestionResponseDto;
import com.forum.project.presentation.question.dto.QuestionUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/questions")
@RequiredArgsConstructor
public class QuestionCrudController {
    private final QuestionCrudService questionCrudService;
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/post")
    public ResponseEntity<QuestionResponseDto> postQuestion(
            @RequestBody QuestionRequestDto questionRequestDto,
            @RequestHeader(value = "Authorization") String header
    ) {
        User user = authenticationService.extractUserByHeader(header);
        QuestionCreateDto questionCreateDto = QuestionDtoFactory.toCreateDto(questionRequestDto, user);
        QuestionResponseDto response = questionCrudService.create(questionCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<QuestionResponseDto> readQuestion(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization") String header
    ) {
        Long userId = authenticationService.extractUserId(header);
        QuestionResponseDto questionResponseDto = questionCrudService.readQuestion(id, userId);
        return ResponseEntity.status(HttpStatus.OK).body(questionResponseDto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<QuestionResponseDto> updateQuestion(
            @PathVariable("id") Long questionId,
            @RequestBody QuestionRequestDto dto,
            @RequestHeader(value = "Authorization") String header
    ) {
        Long userId = authenticationService.extractUserId(header);
        QuestionResponseDto questionResponseDto = questionCrudService.updateTitleAndContent(
                questionId, userId, dto.getTitle(), dto.getContent());
        return ResponseEntity.status(HttpStatus.OK).body(questionResponseDto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<BaseResponseDto> deleteQuestion(
            @PathVariable("id") Long questionId,
            @RequestHeader(value = "Authorization") String header
    ) {
        Long userId = authenticationService.extractUserId(header);
        questionCrudService.delete(questionId, userId);

        BaseResponseDto response = new BaseResponseDto("Question deleted successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
