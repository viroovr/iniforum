package com.forum.project.infrastructure.jwt;

import com.forum.project.application.jwt.TokenService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RefreshRedisTokenBlacklistHandler extends AbstractRedisTokenBlacklistHandler implements TokenBlacklistHandler{

    private final static String REDIS_PREFIX = "blacklist:refreshToken:";

    public RefreshRedisTokenBlacklistHandler(StringRedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String getRedisPrefix() {
        return REDIS_PREFIX;
    }
}