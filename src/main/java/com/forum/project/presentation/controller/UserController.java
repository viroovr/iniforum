package com.forum.project.presentation.controller;

import com.forum.project.application.io.FileService;
import com.forum.project.application.question.QuestionService;
import com.forum.project.application.security.jwt.TokenService;
import com.forum.project.application.user.UserService;
import com.forum.project.presentation.dtos.question.ResponseQuestionDto;
import com.forum.project.presentation.dtos.user.UserInfoDto;
import com.forum.project.presentation.dtos.user.UserRequestDto;
import com.forum.project.presentation.dtos.user.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final TokenService tokenService;

    private final FileService fileService;

    private final QuestionService questionService;

    @GetMapping(value = "/profile")
    public ResponseEntity<UserInfoDto> getUserProfile(
            @RequestHeader("Authorization") String header
    ) {
        String token = tokenService.extractTokenByHeader(header);
        UserInfoDto user = userService.getUserProfile(token);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> updateUserProfile (
            @RequestHeader("Authorization") String header,
            @ModelAttribute UserRequestDto userRequestDto,
            @RequestParam(value = "profileImage", required = false) MultipartFile file
    ) throws IOException{
        String uploadDir = fileService.uploadFile(file);
        String token = tokenService.extractTokenByHeader(header);
        UserResponseDto userResponseDto = userService.updateUserProfile(token, userRequestDto, uploadDir);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }

    @GetMapping(value = "/questions")
    public Page<ResponseQuestionDto> getQuestionsByUserId(
            @RequestHeader(value = "Authorization") String header,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String accessToken = tokenService.extractTokenByHeader(header);
        return questionService.getQuestionsByUser(page, size, accessToken);
    }
}
