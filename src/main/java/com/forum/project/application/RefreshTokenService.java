package com.forum.project.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void saveRefreshToken(String refreshToken, Long userId, ZonedDateTime expiryDate) {
        long ttl = Duration.between(ZonedDateTime.now(), expiryDate).getSeconds();
        redisTemplate.opsForValue().set(refreshToken, String.valueOf(userId), ttl, TimeUnit.SECONDS);
    }

    public void invalidateRefreshToken(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(refreshToken));
    }

    public Long getUserIdFromRefreshToken(String refreshToken) {
        String userId = redisTemplate.opsForValue().get(refreshToken);
        return userId != null ? Long.parseLong(userId) : null;
    }
}
