package com.forum.project.application.jwt;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.user.UserRole;
import com.forum.project.infrastructure.jwt.JwtUtils;
import com.forum.project.presentation.user.UserInfoDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TokenService {

    private final JwtUtils jwtUtils;
    private final Clock clock;
    @Getter
    private final long accessTokenExpTime;
    @Getter
    private final long refreshTokenExpTime;
    @Getter
    private final long passwordResetTokenExpTime;


    public TokenService(
            JwtUtils jwtUtils,
            Clock clock,
            @Value("${jwt.access_token_expiration_time}") long accessTokenExpTime,
            @Value("${jwt.refresh_token_expiration_time}") long refreshTokenExpTime,
            @Value("${jwt.password_reset_token_expiration_time}") long passwordResetTokenExpTime
    ) {
        this.jwtUtils = jwtUtils;
        this.clock = clock;
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
        this.passwordResetTokenExpTime = passwordResetTokenExpTime;
    }

    public Long getId(String token) {
        return jwtUtils.parseClaims(token).get("id", Long.class);
    }

    public String getLoginId(String token) {
        return jwtUtils.parseClaims(token).get("loginId", String.class);
    }

    public UserRole getUserRole(String token) {
        return jwtUtils.parseClaims(token).get("role", UserRole.class);
    }

    public <T> T getClaim(String token, String claimKey, Class<T> claimType) {
        return jwtUtils.parseClaims(token).get(claimKey, claimType);
    }

    private Map<String, Object> makeMapClaims(UserInfoDto member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", member.getId());
        claims.put("loginId", member.getLoginId());
        claims.put("email", member.getEmail());
        claims.put("role", member.getRole());
        return claims;
    }

    public String createAccessToken(UserInfoDto member) {
        Map<String, Object> claims = makeMapClaims(member);
        return jwtUtils.createToken(claims, accessTokenExpTime);
    }

    public String createRefreshToken(UserInfoDto member) {
        Map<String, Object> claims = makeMapClaims(member);
        return jwtUtils.createToken(claims, refreshTokenExpTime);
    }

    public String createPasswordResetToken(UserInfoDto member) {
        Map<String, Object> claims = makeMapClaims(member);
        return jwtUtils.createToken(claims, passwordResetTokenExpTime);
    }

    public long getExpirationTime(String token) {
        Date expiration = jwtUtils.parseClaims(token).getExpiration();
        long currentMillis = clock.millis();
        return (expiration.getTime() - currentMillis) / 1000;
    }

    public boolean isValidToken(String token) {
        return jwtUtils.isValidToken(token);
    }

    /**
        Refresh Token Rotation:
        Refresh Token을 사용할 때마다 새로운 Refresh Token을 발급하며,
         이전 Refresh Token은 즉시 만료 처리합니다.
     */
    public String regenerateAccessToken(String refreshToken) {
        UserInfoDto userInfoDto = jwtUtils.extractUserInfo(refreshToken);
        return createAccessToken(userInfoDto);
    }

    public String regenerateRefreshToken(String refreshToken) {
        UserInfoDto userInfoDto = jwtUtils.extractUserInfo(refreshToken);
        return createRefreshToken(userInfoDto);
    }

    public String extractTokenByHeader(String authorizationHeader) {
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new ApplicationException(ErrorCode.INVALID_AUTH_HEADER);
    }
}
