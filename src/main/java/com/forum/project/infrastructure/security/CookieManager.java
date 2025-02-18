package com.forum.project.infrastructure.security;

import com.forum.project.domain.auth.dto.CookieBuilder;
import com.forum.project.domain.auth.service.TokenService;
import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
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
        return new CookieBuilder("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge((int) tokenService.getRefreshTokenExpTime())
                .build();
    }

    public Cookie createEmtpyRefreshTokenCookie() {
        return new CookieBuilder("refreshToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
    }

    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        return request.getCookies() == null ? null : Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_REFRESH_TOKEN,
                        "쿠키에 refreshToken 이름이 존재하지 않습니다."));
    }
}
