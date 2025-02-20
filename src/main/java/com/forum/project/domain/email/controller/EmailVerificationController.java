package com.forum.project.domain.email.controller;

import com.forum.project.core.base.BaseResponseDto;
import com.forum.project.domain.auth.dto.EmailVerificationConfirmDto;
import com.forum.project.domain.auth.dto.EmailVerificationRequestDto;
import com.forum.project.domain.email.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/email-verification")
@RequiredArgsConstructor
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/request")
    public ResponseEntity<BaseResponseDto> requestEmailVerification(
            @Valid @RequestBody EmailVerificationRequestDto dto
    ) {
        emailVerificationService.sendVerificationCode(dto.getEmail());
        return BaseResponseDto.buildOkResponse("Verification email sent successfully.");
    }

    @PostMapping("/confirm")
    public ResponseEntity<BaseResponseDto> confirmEmailVerification(
            @Valid @RequestBody EmailVerificationConfirmDto dto
    ) {
        emailVerificationService.verifyEmailCode(dto.getEmail(), dto.getCode());
        return BaseResponseDto.buildOkResponse("Email verified successfully.");
    }
}
