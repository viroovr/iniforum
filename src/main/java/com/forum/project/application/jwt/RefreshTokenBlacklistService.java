package com.forum.project.application.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenBlacklistService extends AbstractTokenBlacklistService{

    private final static String REDIS_PREFIX = "blacklist:refreshToken:";

    public RefreshTokenBlacklistService(StringRedisTemplate redisTemplate, TokenService tokenService) {
        super(redisTemplate, tokenService);
    }

    @Override
    protected String getRedisPrefix() {
        return REDIS_PREFIX;
    }
}