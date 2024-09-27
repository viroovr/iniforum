package com.forum.project.presentation.auth;

import com.forum.project.application.CookieService;
import com.forum.project.application.RefreshTokenService;
import com.forum.project.application.auth.AuthService;
import com.forum.project.application.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    public AuthController(AuthService authService, CookieService cookieService) {
        this.authService = authService;
        this.cookieService = cookieService;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> requestSignup(
            @Valid @RequestBody SignupRequestDto signupRequestDto
    ) {
        SignupResponseDto createdUser = authService.createUser(signupRequestDto);
        return ResponseEntity.ok("User created");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> requestLogin(
            @Valid @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response
    ) {
        Map<String, String> tokens = authService.loginUserWithTokens(loginRequestDto);

        Cookie refreshTokenCookie = cookieService.createRefreshTokenCookie(tokens.get("refreshToken"));
        cookieService.addCookieToResponse(response, refreshTokenCookie);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("accessToken", tokens.get("accessToken")));
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String token,
            HttpServletRequest request,
            HttpServletResponse response

    ) {
        String jwt = token.substring(7);

        long expirationTime = authService.getJwtExpirationTime(jwt);
        String refreshToken = cookieService.getRefreshTokenFromCookies(request);

        if(refreshToken == null || !authService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Logged out failed");
        }

        authService.logout(jwt, refreshToken, expirationTime);

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
