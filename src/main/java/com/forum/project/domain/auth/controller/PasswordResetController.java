package com.forum.project.domain.auth.controller;

import com.forum.project.core.base.BaseResponseDto;
import com.forum.project.domain.auth.dto.PasswordResetRequestDto;
import com.forum.project.domain.auth.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/request")
    public ResponseEntity<BaseResponseDto> requestPasswordReset(@RequestParam String email) {
        passwordResetService.sendNewResetTokenToEmail(email);
        return BaseResponseDto.buildOkResponse("Reset password send successfully.");
    }

    @PostMapping("/reset")
    public ResponseEntity<BaseResponseDto> resetPassword(@RequestBody PasswordResetRequestDto dto) {
        passwordResetService.resetPassword(dto);
        return BaseResponseDto.buildOkResponse("Password reset successfully");
    }
}