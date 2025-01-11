package com.forum.project.application.jwt;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.user.UserRole;
import com.forum.project.infrastructure.jwt.ClaimRequestDto;
import com.forum.project.infrastructure.jwt.JwtUtils;
import com.forum.project.infrastructure.jwt.TokenCacheHandler;
import com.forum.project.presentation.user.UserInfoDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Date;

@Slf4j
@Service
public class TokenService {

    private final TokenCacheHandler tokenCacheHandler;
    private final JwtUtils jwtUtils;
    private final Clock clock;
    @Getter
    private final long accessTokenExpTime;
    @Getter
    private final long refreshTokenExpTime;
    @Getter
    private final long passwordResetTokenExpTime;

    public TokenService(
            TokenCacheHandler tokenCacheHandler,
            JwtUtils jwtUtils,
            Clock clock,
            @Value("${jwt.access_token_expiration_time}") long accessTokenExpTime,
            @Value("${jwt.refresh_token_expiration_time}") long refreshTokenExpTime,
            @Value("${jwt.password_reset_token_expiration_time}") long passwordResetTokenExpTime
    ) {
        this.tokenCacheHandler = tokenCacheHandler;
        this.jwtUtils = jwtUtils;
        this.clock = clock;
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
        this.passwordResetTokenExpTime = passwordResetTokenExpTime;
    }

    private <T> T extractClaim(String token, String claimKey, Class<T> claimType) {
        return tokenCacheHandler.extractClaim(token, claimKey, claimType);
    }

    public Long getUserId(String token) {
        return extractClaim(token, ClaimRequestDto.USER_ID_CLAIM_KEY, Long.class);
    }

    public String getLoginId(String token) {
        return extractClaim(token,ClaimRequestDto.LOGIN_ID_CLAIM_KEY, String.class);
    }

    public String getUserRole(String token) {
        return extractClaim(token,ClaimRequestDto.USER_ROLE_CLAIM_KEY, String.class);
    }

    public long getExpirationTime(String token) {
        Date expiration = tokenCacheHandler.getExpirationDate(token);
        long currentMillis = clock.millis();
        return (expiration.getTime() - currentMillis) / 1000;
    }

    public boolean hasRole(String token, UserRole role) {
        String tokenRole = getUserRole(token);
        return role.name().equals(tokenRole);
    }

    public boolean isValidToken(String token) {
        return jwtUtils.isValidToken(token);
    }

    private String createToken(ClaimRequestDto dto, long expirationTime) {
        return jwtUtils.createToken(dto.toMap(), expirationTime);
    }

    public String createAccessToken(UserInfoDto member) {
        return createToken(new ClaimRequestDto(member), accessTokenExpTime);
    }

    public String createRefreshToken(UserInfoDto member) {
        return createToken(new ClaimRequestDto(member), refreshTokenExpTime);
    }

    public String createPasswordResetToken(UserInfoDto member) {
        return createToken(new ClaimRequestDto(member), passwordResetTokenExpTime);
    }

    public String regenerateAccessToken(String refreshToken) {
        ClaimRequestDto dto = tokenCacheHandler.extractClaimsByToken(refreshToken);
        return createToken(dto, accessTokenExpTime);
    }

    public String regenerateRefreshToken(String refreshToken) {
        ClaimRequestDto dto = tokenCacheHandler.extractClaimsByToken(refreshToken);
        return createToken(dto, refreshTokenExpTime);
    }
}
