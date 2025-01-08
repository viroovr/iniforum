package com.forum.project.application.jwt;

import com.forum.project.infrastructure.jwt.AccessRedisTokenBlacklistHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessTokenBlacklistHandlerTest {
    @Mock
    private StringRedisTemplate redisTemplate;

    @InjectMocks
    private AccessRedisTokenBlacklistHandler accessTokenBlacklistService;

    private final static String REDIS_KEY = "blacklist:accessToken:";

    @Test
    void shouldBlacklistAccessTokenSuccessfully_whenValidTokenProvided() {
        String testAccessToken = "valid-access-token";
        long expiryTime = 3600L;
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        accessTokenBlacklistService.blacklistToken(testAccessToken, expiryTime);

        verify(redisTemplate.opsForValue()).set(
                eq(REDIS_KEY + testAccessToken),
                eq("blacklisted"),
                eq(expiryTime),
                eq(TimeUnit.SECONDS));
    }

    @Test
    void shouldReturnFalse_whenAccessTokenIsNotBlacklisted() {
        String testAccessToken = "valid-access-token";
        String blacklistKey = REDIS_KEY + testAccessToken;
        when(redisTemplate.hasKey(blacklistKey)).thenReturn(false);

        boolean isBlacklisted = accessTokenBlacklistService.isBlacklistedToken(testAccessToken);

        assertFalse(isBlacklisted);
    }

    @Test
    void shouldReturnTrue_whenAccessTokenIsBlacklisted() {
        String testAccessToken = "valid-access-token";
        String blacklistKey = REDIS_KEY + testAccessToken;
        when(redisTemplate.hasKey(blacklistKey)).thenReturn(true);

        boolean isBlacklisted = accessTokenBlacklistService.isBlacklistedToken(testAccessToken);

        assertTrue(isBlacklisted);
    }
}