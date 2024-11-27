package com.forum.project.application.security.jwt;

import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.ErrorCode;
import com.forum.project.domain.exception.InvalidTokenException;
import com.forum.project.presentation.dtos.user.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    private final TokenService tokenService;

    public void saveRefreshToken(String refreshToken, Long userId) {
        ZonedDateTime expiryDate = ZonedDateTime.now().plusSeconds(tokenService.getRefreshTokenExpTime());
        long ttl = Duration.between(ZonedDateTime.now(), expiryDate).getSeconds();

        redisTemplate.opsForValue().set(refreshToken, String.valueOf(userId), ttl, TimeUnit.SECONDS);
    }

    public void validateToken(String refreshToken) {
        if (isBlacklistedRefreshToken(refreshToken)) {
            throw new ApplicationException(ErrorCode.AUTH_BLACKLISTED_REFRESH_TOKEN);
        }
        if (tokenService.validateToken(refreshToken)) {
            throw new ApplicationException(ErrorCode.AUTH_INVALID_TOKEN);
        }
    }

    public void invalidateToken(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }

    public boolean isBlacklistedRefreshToken(String refreshToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(refreshToken));
    }

    public String createRefreshToken(UserInfoDto userInfoDto) {
        String refreshToken = tokenService.createRefreshToken(userInfoDto);
        saveRefreshToken(refreshToken, userInfoDto.getId());
        return refreshToken;
    }

//    public Long getUserIdFromRefreshToken(String refreshToken) {
//        String userId = redisTemplate.opsForValue().get(refreshToken);
//        return userId != null ? Long.parseLong(userId) : null;
//    }
}