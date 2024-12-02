package com.forum.project.presentation.controller;

import com.forum.project.application.CookieService;
import com.forum.project.application.auth.AuthService;
import com.forum.project.application.auth.EmailService;
import com.forum.project.presentation.dtos.auth.LoginRequestDto;
import com.forum.project.presentation.dtos.auth.SignupRequestDto;
import com.forum.project.presentation.dtos.auth.SignupResponseDto;
import com.forum.project.presentation.dtos.token.TokenResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    private final CookieService cookieService;

    private final EmailService emailService;

    @PostMapping("/send-email")
    public ResponseEntity<Map<String, String>> sendVerificationEmail(
            @RequestBody Map<String, String> request
    ) {
        String email = request.get("email");
        emailService.sendVerificationCode(email);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Verification email sent");

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmailCode(
            @RequestBody Map<String, String> request
    ) {
        String email = request.get("email");
        String code = request.get("code");

        emailService.verifyCode(email, code);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Email verified successfully");

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<SignupResponseDto> requestSignup(
            @Valid @RequestBody SignupRequestDto signupRequestDto
    ) {
        emailService.verifyEmail(signupRequestDto.getEmail());

        SignupResponseDto createdUser = authService.createUser(signupRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(createdUser);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<TokenResponseDto> requestLogin(
            @Valid @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response
    ) {
        TokenResponseDto tokens = authService.loginUserWithTokens(loginRequestDto);

        Cookie refreshTokenCookie = cookieService.createRefreshTokenCookie(tokens.getRefreshToken());

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.status(HttpStatus.OK).body(tokens);
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.logout(
                authHeader,
                cookieService.getRefreshTokenFromCookies(request)
        );

        cookieService.clearRefreshToken(response);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Logged out successfully");

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshAccessToken(
            HttpServletRequest request
    ) {
        String refreshToken = cookieService.getRefreshTokenFromCookies(request);

        return ResponseEntity.status(HttpStatus.OK).body(
                authService.refreshAccessToken(refreshToken)
        );
    }

}
