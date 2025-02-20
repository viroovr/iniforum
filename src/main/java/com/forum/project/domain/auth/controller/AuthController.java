package com.forum.project.domain.auth.controller;

import com.forum.project.core.base.BaseResponseDto;
import com.forum.project.core.common.TokenUtil;
import com.forum.project.domain.auth.dto.*;
import com.forum.project.domain.auth.service.AuthService;
import com.forum.project.domain.email.service.EmailVerificationService;
import com.forum.project.infrastructure.security.CookieManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final CookieManager cookieManager;
    private final EmailVerificationService emailVerificationService;

    @PostMapping(value = "/signup")
    public ResponseEntity<SignupResponseDto> requestSignup(
            @Valid @RequestBody SignupRequestDto dto
    ) {
        emailVerificationService.validateEmailCode(dto.getEmail());

        SignupResponseDto responseBody = authService.createUser(dto);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<TokenResponseDto> requestLogin(
            @Valid @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response
    ) {
        TokenResponseDto responseDto = authService.loginUserWithTokens(loginRequestDto);

        response.addCookie(cookieManager.createRefreshTokenCookie(responseDto.getRefreshToken()));
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    private TokenRequestDto extractTokens(String header, HttpServletRequest request) {
        String accessToken = TokenUtil.extractToken(header);
        String refreshToken = cookieManager.getRefreshTokenFromCookies(request);
        return new TokenRequestDto(accessToken, refreshToken);
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<BaseResponseDto> logout(
            @RequestHeader("Authorization") String header,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.logout(extractTokens(header, request));
        response.addCookie(cookieManager.createEmtpyRefreshTokenCookie());

        return BaseResponseDto.buildOkResponse("Logged out successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshAccessToken(
            @RequestHeader("Authorization") String header,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        TokenResponseDto responseDto = authService.refreshAccessToken(extractTokens(header, request));

        response.addCookie(cookieManager.createRefreshTokenCookie(responseDto.getRefreshToken()));

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
