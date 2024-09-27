package com.forum.project.presentation.auth;

import com.forum.project.application.RefreshTokenService;
import com.forum.project.application.auth.AuthService;
import com.forum.project.application.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> requestSignup(
            @Valid @RequestBody SignupRequestDto signupRequestDto
    ) {
        SignupResponseDto createdUser = authService.createUser(signupRequestDto);
        if (createdUser != null) {
            return ResponseEntity.ok("User created");
        } else {
            return ResponseEntity.badRequest().body("User Created Failed");
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> requestLogin(
            @Valid @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response
    ) {
        Map<String, String> tokens = authService.loginUserWithTokens(loginRequestDto);
        String refreshToken = tokens.get("refreshToken");

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("accessToken", tokens.get("accessToken")));
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String token,
            HttpServletRequest request,
            HttpServletResponse response

    ) {
        String jwt = token.substring(7);
        long expirationTime = jwtTokenProvider.getExpirationTime(jwt);

        String refreshToken = getRefreshTokenFromCookies(request);
        System.out.println(refreshToken);
        if(refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Logged out failed");
        }
        authService.logout(jwt, refreshToken, expirationTime);

        clearRefreshTokenCookie(response);

        return ResponseEntity.ok("Logged out successfully");
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(
            HttpServletRequest request
    ) {
        String refreshToken = getRefreshTokenFromCookies(request);
        System.out.println(refreshToken);
        if(refreshToken == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh Token is missing");
        }
        if(!refreshTokenService.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh Token is Blacklisted");
        }
        try {
            String newAccessToken = jwtTokenProvider.regenerateAccessToken(refreshToken);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Refresh Token");
        }
    }



}
