package com.forum.project.infrastructure.redis.implementation;

import com.forum.project.domain.auth.repository.TokenBlacklistHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TokenBlacklistHandlerRedisImpl implements TokenBlacklistHandler {
    private final RedisTemplate<String, Object> redisTemplateObject;

    private final String REDIS_PREFIX_REFRESH_TOKEN = "blacklist:refreshToken:";
    private final String REDIS_PREFIX_ACCESS_TOKEN = "blacklist:accessToken:";

    private void blacklistToken(String key, long ttl) {
        redisTemplateObject.opsForValue().set(
                key,
                "blacklisted",
                ttl, TimeUnit.SECONDS);
    }

    private boolean isBlacklistedToken(String key) {
        return Boolean.TRUE.equals(redisTemplateObject.hasKey(key));
    }

    @Override
    public void blacklistAccessToken(String token, long ttl) {
        blacklistToken(REDIS_PREFIX_ACCESS_TOKEN + token, ttl);
    }

    @Override
    public void blacklistRefreshToken(String token, long ttl) {
        blacklistToken(REDIS_PREFIX_REFRESH_TOKEN + token, ttl);
    }

    @Override
    public boolean isBlacklistedAccessToken(String token) {
        return isBlacklistedToken(REDIS_PREFIX_ACCESS_TOKEN + token);
    }

    @Override
    public boolean isBlacklistedRefreshToken(String token) {
        return isBlacklistedToken(REDIS_PREFIX_REFRESH_TOKEN + token);
    }
}
