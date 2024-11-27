package com.forum.project.application;

import com.forum.project.application.security.jwt.TokenService;
import com.forum.project.domain.exception.RefreshTokenNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CookieService {

    private final TokenService tokenService;

    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) tokenService.getRefreshTokenExpTime());
//        refreshTokenCookie.setSecure(true); //Https 에서만 전송

        return refreshTokenCookie;
    }

    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        return request.getCookies() == null ? null : Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token cookie not found"));
    }

    public void clearRefreshToken(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
