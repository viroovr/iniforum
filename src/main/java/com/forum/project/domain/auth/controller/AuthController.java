package com.forum.project.domain.auth.controller;

import com.forum.project.domain.auth.dto.EmailRequestDto;
import com.forum.project.domain.auth.dto.LoginRequestDto;
import com.forum.project.domain.auth.dto.SignupRequestDto;
import com.forum.project.domain.auth.dto.SignupResponseDto;
import com.forum.project.infrastructure.security.CookieManager;
import com.forum.project.domain.auth.service.AuthService;
import com.forum.project.domain.auth.service.EmailVerificationService;
import com.forum.project.domain.auth.service.TokenService;
import com.forum.project.core.base.BaseResponseDto;
import com.forum.project.domain.auth.dto.TokenResponseDto;
import jakarta.servlet.http.Cookie;
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

    @PostMapping("/send-email")
    public ResponseEntity<BaseResponseDto> sendVerificationEmail(
            @Valid @RequestBody EmailRequestDto emailRequestDto
    ) {
        emailVerificationService.sendVerificationCode(emailRequestDto.getEmail());

        BaseResponseDto response = new BaseResponseDto("Verification email sent");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<BaseResponseDto> verifyEmailCode(
            @RequestBody EmailRequestDto emailRequestDto
    ) {
        String email = emailRequestDto.getEmail();
        String code = emailRequestDto.getCode();

        emailVerificationService.verifyEmailCode(email, code);

        BaseResponseDto responseBody = new BaseResponseDto("Email verified successfully");

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<SignupResponseDto> requestSignup(
            @Valid @RequestBody SignupRequestDto signupRequestDto
    ) {
        emailVerificationService.validateEmailCode(signupRequestDto.getEmail());

        SignupResponseDto responseBody = authService.createUser(signupRequestDto);
        responseBody.setMessage("Signup successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<TokenResponseDto> requestLogin(
            @Valid @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response
    ) {
        TokenResponseDto responseDto = authService.loginUserWithTokens(loginRequestDto);

        Cookie refreshTokenCookie = cookieManager.createRefreshTokenCookie(responseDto.getRefreshToken());

        response.addCookie(refreshTokenCookie);
        responseDto.setMessage("Login successfully");

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    private void clearRefreshToken(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<BaseResponseDto> logout(
            @RequestHeader("Authorization") String header,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = cookieManager.getRefreshTokenFromCookies(request);

        authService.logout(refreshToken, header);
        clearRefreshToken(response);

        BaseResponseDto responseDto = new BaseResponseDto("Logged out successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshAccessToken(
            @RequestHeader("Authorization") String header,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = cookieManager.getRefreshTokenFromCookies(request);

        TokenResponseDto responseDto = authService.refreshAccessToken(refreshToken, header);
        Cookie refreshTokenCookie = cookieManager.createRefreshTokenCookie(responseDto.getRefreshToken());
        response.addCookie(refreshTokenCookie);
        responseDto.setMessage("Refresh access token successfully");

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
