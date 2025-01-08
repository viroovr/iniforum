package com.forum.project.infrastructure.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract class AbstractRedisTokenBlacklistHandler {
    private final StringRedisTemplate redisTemplate;

    protected abstract String getRedisPrefix();

    public void blacklistToken(String token, long ttl) {
        String blacklistKey = getRedisPrefix() + token;
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
