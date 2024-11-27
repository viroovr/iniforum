package com.forum.project.application.security.jwt;

import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.ErrorCode;
import com.forum.project.presentation.dtos.user.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AccessTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final TokenService tokenService;

    public void validateToken(String accessToken) {
        if (isBlacklisted(accessToken)) {
            throw new ApplicationException(ErrorCode.AUTH_BLACKLISTED_REFRESH_TOKEN);
        }
        if (tokenService.validateToken(accessToken)) {
            throw new ApplicationException(ErrorCode.AUTH_INVALID_TOKEN);
        }
    }

    public void invalidateToken(String token) {
        long expirationTime = tokenService.getExpirationTime(token);;
        redisTemplate.opsForValue().set(token, true, expirationTime, TimeUnit.SECONDS);
    }

    public Object getBlackList(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean isBlacklisted(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public String createAccessToken(UserInfoDto userInfoDto) {
        return tokenService.createAccessToken(userInfoDto);
    }

    public String extractTokenByHeader(String header) {
        return tokenService.extractTokenByHeader(header);
    }

}
