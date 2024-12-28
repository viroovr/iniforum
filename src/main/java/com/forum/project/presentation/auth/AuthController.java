package com.forum.project.presentation.auth;

import com.forum.project.application.CookieService;
import com.forum.project.application.auth.AuthService;
import com.forum.project.application.email.EmailVerificationService;
import com.forum.project.application.jwt.TokenService;
import com.forum.project.presentation.dtos.BaseResponseDto;
import com.forum.project.presentation.dtos.TokenResponseDto;
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
    private final CookieService cookieService;
    private final EmailVerificationService emailVerificationService;
    private final TokenService tokenService;

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

        emailVerificationService.verifyCode(email, code);

        BaseResponseDto responseBody = new BaseResponseDto("Email verified successfully");

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<SignupResponseDto> requestSignup(
            @Valid @RequestBody SignupRequestDto signupRequestDto
    ) {
        emailVerificationService.verifyEmail(signupRequestDto.getEmail());

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

        Cookie refreshTokenCookie = cookieService.createRefreshTokenCookie(responseDto.getRefreshToken());

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
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String accessToken = tokenService.extractTokenByHeader(authHeader);
        String refreshToken = cookieService.getRefreshTokenFromCookies(request);

        authService.logout(accessToken,refreshToken);
        clearRefreshToken(response);

        BaseResponseDto responseDto = new BaseResponseDto("Logged out successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshAccessToken(
            HttpServletRequest request
    ) {
        String refreshToken = cookieService.getRefreshTokenFromCookies(request);

        TokenResponseDto responseDto = authService.refreshAccessToken(refreshToken);
        responseDto.setMessage("Refresh accessToken successfully");

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
