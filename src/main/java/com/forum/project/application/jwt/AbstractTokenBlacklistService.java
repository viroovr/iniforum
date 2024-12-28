package com.forum.project.application.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract class AbstractTokenBlacklistService {
    private final StringRedisTemplate redisTemplate;
    private final TokenService tokenService;

    protected abstract String getRedisPrefix();

    public void blacklistToken(String accessToken) {
        long ttl = tokenService.getExpirationTime(accessToken);
        String blacklistKey = getRedisPrefix() + accessToken;
        redisTemplate.opsForValue().set(
                blacklistKey,
                "blacklisted",
                ttl, TimeUnit.SECONDS);
    }

    public boolean isBlacklistedToken(String token) {
        String blacklistKey = getRedisPrefix() + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey));
    }
}
