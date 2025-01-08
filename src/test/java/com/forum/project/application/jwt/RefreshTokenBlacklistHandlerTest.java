package com.forum.project.application.jwt;

import com.forum.project.infrastructure.jwt.RefreshRedisTokenBlacklistHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenBlacklistHandlerTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @InjectMocks
    private RefreshRedisTokenBlacklistHandler refreshTokenBlacklistService;

    private final static String REDIS_KEY = "blacklist:refreshToken:";

    @Test
    void shouldBlacklistRefreshTokenSuccessfully_whenValidTokenProvided() {
        String testRefreshToken = "valid-refresh-token";
        long expiryTime = 3600L;
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        refreshTokenBlacklistService.blacklistToken(testRefreshToken, expiryTime);

        verify(redisTemplate.opsForValue()).set(
                eq(REDIS_KEY + testRefreshToken),
                eq("blacklisted"),
                eq(expiryTime),
                eq(TimeUnit.SECONDS));

    }

    @Test
    void shouldReturnFalse_whenRefreshTokenIsNotBlacklisted() {
        String testRefreshToken = "valid-refresh-token";
        String blacklistKey = REDIS_KEY + testRefreshToken;
        when(redisTemplate.hasKey(blacklistKey)).thenReturn(false);

        boolean isBlacklisted = refreshTokenBlacklistService.isBlacklistedToken(testRefreshToken);

        assertFalse(isBlacklisted);
    }

    @Test
    void shouldReturnTrue_whenRefreshTokenIsBlacklisted() {
        String testRefreshToken = "valid-refresh-token";
        String blacklistKey = REDIS_KEY + testRefreshToken;
        when(redisTemplate.hasKey(blacklistKey)).thenReturn(true);

        boolean isBlacklisted = refreshTokenBlacklistService.isBlacklistedToken(testRefreshToken);

        assertTrue(isBlacklisted);
    }
}