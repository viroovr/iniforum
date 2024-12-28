package com.forum.project.application.jwt;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenBlacklistService extends AbstractTokenBlacklistService{

    private final static String REDIS_PREFIX = "blacklist:accessToken:";

    public AccessTokenBlacklistService(StringRedisTemplate redisTemplate, TokenService tokenService) {
        super(redisTemplate, tokenService);
    }

    @Override
    protected String getRedisPrefix() {
        return REDIS_PREFIX;
    }
}
