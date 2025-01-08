package com.forum.project.infrastructure.security;

import com.forum.project.application.jwt.TokenService;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CookieManager {

    private final TokenService tokenService;

    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) tokenService.getRefreshTokenExpTime());

        return refreshTokenCookie;
    }

    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        return request.getCookies() == null ? null : Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_REFRESH_TOKEN));
    }
}
