package com.forum.project.application;

import com.forum.project.application.jwt.TokenService;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.infrastructure.security.CookieManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CookieManagerTest {

    @InjectMocks
    private CookieManager cookieManager;

    @Mock
    private TokenService tokenService;

    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String COOKIE_NAME = "refreshToken";

    @Test
    public void testCreateRefreshTokenCookie_Success() {
        long refreshTokenExpTime = 604800L;
        when(tokenService.getRefreshTokenExpTime()).thenReturn(refreshTokenExpTime);

        Cookie cookie = cookieManager.createRefreshTokenCookie(REFRESH_TOKEN);

        assertNotNull(cookie);
        assertEquals(COOKIE_NAME, cookie.getName());
        assertEquals(REFRESH_TOKEN, cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
        assertEquals(refreshTokenExpTime, cookie.getMaxAge());
    }

    @Test
    public void testGetRefreshTokenFromCookies_Success() {
        Cookie refreshTokenCookie = new Cookie(COOKIE_NAME, REFRESH_TOKEN);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie});

        String refreshToken = cookieManager.getRefreshTokenFromCookies(request);

        assertEquals(REFRESH_TOKEN, refreshToken);
    }

    @Test
    public void testGetRefreshTokenFromCookies_ThrowsException_WhenCookieNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[]{});

        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            cookieManager.getRefreshTokenFromCookies(request);
        });

        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());
    }
}