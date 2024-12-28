package com.forum.project.application.jwt;

import com.forum.project.domain.user.UserRole;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.presentation.user.UserInfoDto;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TokenService {

    private final JwtUtils jwtUtils;
    private final Clock clock;
    @Getter
    private final long accessTokenExpTime;
    @Getter
    private final long refreshTokenExpTime;


    public TokenService(
            JwtUtils jwtUtils,
            Clock clock,
            @Value("${jwt.access_token_expiration_time}") long accessTokenExpTime,
            @Value("${jwt.refresh_token_expiration_time}") long refreshTokenExpTime
    ) {
        this.jwtUtils = jwtUtils;
        this.clock = clock;
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
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

    public String createAccessToken(UserInfoDto member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", member.getId());
        claims.put("loginId", member.getLoginId());
        claims.put("email", member.getEmail());
        claims.put("role", member.getRole());
        return jwtUtils.createToken(claims, accessTokenExpTime);
    }

    public String createRefreshToken(UserInfoDto member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", member.getId());
        claims.put("loginId", member.getLoginId());
        claims.put("email", member.getEmail());
        claims.put("role", member.getRole());
        return jwtUtils.createToken(claims, refreshTokenExpTime);
    }

    public long getExpirationTime(String token) {
        Date expiration = jwtUtils.parseClaims(token).getExpiration();
        long currentMillis = clock.millis();
        return (expiration.getTime() - currentMillis) / 1000;
    }

    public boolean isValidToken(String token) {
        return jwtUtils.isValidToken(token);
    }

    public String regenerateAccessToken(String refreshToken) {
        Claims claims = jwtUtils.parseClaims(refreshToken);
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .id(claims.get("id", Long.class))
                .loginId(claims.get("loginId", String.class))
                .email(claims.get("email", String.class))
                .role(claims.get("role", String.class))
                .build();

        return createAccessToken(userInfoDto);
    }

    public String extractTokenByHeader(String authorizationHeader) {
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new ApplicationException(ErrorCode.INVALID_AUTH_HEADER);
    }
}
