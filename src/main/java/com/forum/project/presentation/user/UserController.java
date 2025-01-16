package com.forum.project.presentation.user;

import com.forum.project.application.user.UserFacade;
import com.forum.project.application.question.QuestionService;
import com.forum.project.presentation.auth.EmailRequestDto;
import com.forum.project.presentation.dtos.BaseResponseDto;
import com.forum.project.presentation.question.QuestionPageResponseDto;
import jakarta.validation.Valid;
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

    private final UserFacade userFacade;
    private final QuestionService questionService;

    @GetMapping(value = "/profile")
    public ResponseEntity<UserInfoDto> getUserProfile(
            @RequestHeader("Authorization") String header
    ) {
        UserInfoDto user = userFacade.getUserProfileByHeader(header);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> updateUserProfile (
            @RequestHeader("Authorization") String header,
            @ModelAttribute UserRequestDto userRequestDto,
            @RequestParam(value = "profileImage", required = false) MultipartFile file
    ) throws IOException{
        UserResponseDto userResponseDto = userFacade.updateUserProfileByHeader(header, userRequestDto, file);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }

    @GetMapping(value = "/questions/{userId}")
    public Page<QuestionPageResponseDto> getQuestionsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return questionService.getQuestionsByUser(userId, page, size);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponseDto> resetPassword(
            @RequestParam("token") String token,
            @RequestBody ResetPasswordRequestDto request
    ) {
        userFacade.resetPassword(token, request.getPassword());
        BaseResponseDto responseDto = new BaseResponseDto("Password reset successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/send/reset-password")
    public ResponseEntity<BaseResponseDto> requestResetPassword(
            @Valid @RequestBody EmailRequestDto request
    ) {
        userFacade.requestPasswordReset(request.getEmail());
        BaseResponseDto responseDto = new BaseResponseDto("Reset password send successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
