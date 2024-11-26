package com.forum.project.presentation.controller;

import com.forum.project.application.CookieService;
import com.forum.project.application.auth.AuthService;
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

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    private final CookieService cookieService;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<SignupResponseDto> requestSignup(
            @Valid @RequestBody SignupRequestDto signupRequestDto
    ) {
        SignupResponseDto createdUser = authService.createUser(signupRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(createdUser);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<TokenResponseDto> requestLogin(
            @Valid @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response
    ) {
        TokenResponseDto tokens = authService.loginUserWithTokens(loginRequestDto);

        Cookie refreshTokenCookie = cookieService.createRefreshTokenCookie(tokens.getRefreshToken());

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.status(HttpStatus.OK).body(tokens);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        long expirationTime = authService.getJwtExpirationTime(token);
        String refreshToken = cookieService.getRefreshTokenFromCookies(request);

        authService.logout(token, refreshToken, expirationTime);

        cookieService.clearRefreshTokenCookie(response);

        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(
            HttpServletRequest request
    ) {
        String refreshToken = cookieService.getRefreshTokenFromCookies(request);
        String newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

}
