package com.forum.project.infrastructure.jwt;

import com.forum.project.application.jwt.TokenService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class AccessRedisTokenBlacklistHandler extends AbstractRedisTokenBlacklistHandler implements TokenBlacklistHandler{

    private final static String REDIS_PREFIX = "blacklist:accessToken:";

    public AccessRedisTokenBlacklistHandler(StringRedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String getRedisPrefix() {
        return REDIS_PREFIX;
    }
}
