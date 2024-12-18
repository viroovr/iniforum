package com.forum.project.application.security.jwt;

import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.ErrorCode;
import com.forum.project.presentation.dtos.user.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AccessTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REDIS_PREFIX = "blacklist:accessToken:";
    private final TokenService tokenService;

    public void checkTokenValidity(String accessToken) {
        if (tokenService.isValidToken(accessToken)) {
            throw new ApplicationException(ErrorCode.AUTH_INVALID_TOKEN);
        }
        if (isBlacklisted(accessToken)) {
            throw new ApplicationException(ErrorCode.AUTH_BLACKLISTED_REFRESH_TOKEN);
        }
    }

    public void revokeToken(String token) {
        long expirationTime = tokenService.getExpirationTime(token);
        redisTemplate.opsForValue().set(REDIS_PREFIX + token, true, expirationTime, TimeUnit.SECONDS);
    }

    public boolean isBlacklisted(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public String createAccessToken(UserInfoDto userInfoDto) {
        return tokenService.createAccessToken(userInfoDto);
    }
}
