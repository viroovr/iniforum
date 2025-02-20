package com.forum.project.domain.auth.service;

import com.forum.project.core.common.ClockUtil;
import com.forum.project.domain.auth.dto.TokenResponseDto;
import com.forum.project.domain.auth.mapper.AuthDtoMapper;
import com.forum.project.domain.auth.vo.ClaimRequest;
import com.forum.project.domain.auth.vo.TokenExpirationProperties;
import com.forum.project.domain.user.dto.UserInfoDto;
import com.forum.project.domain.user.vo.UserRole;
import com.forum.project.infrastructure.jwt.JwtUtils;
import com.forum.project.infrastructure.jwt.TokenCacheHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtUtils jwtUtils;
    private final TokenCacheHandler tokenCacheHandler;
    private final TokenExpirationProperties expirationProperties;

    private <T> T extractClaim(String token, String claimKey, Class<T> claimType) {
        return tokenCacheHandler.extractClaim(token, claimKey, claimType);
    }

    public Long getUserId(String token) {
        return extractClaim(token, ClaimRequest.USER_ID_CLAIM_KEY, Long.class);
    }

    public String getLoginId(String token) {
        return extractClaim(token, ClaimRequest.LOGIN_ID_CLAIM_KEY, String.class);
    }

    public String getUserRole(String token) {
        return extractClaim(token, ClaimRequest.USER_ROLE_CLAIM_KEY, String.class);
    }

    public long getExpirationTime(String token) {
        Date expiration = tokenCacheHandler.getExpirationDate(token);
        long currentMillis = ClockUtil.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        return (expiration.getTime() - currentMillis) / 1000;
    }

    public boolean isAdmin(String token) {
        return hasRole(token, UserRole.ADMIN);
    }

    public boolean isUser(String token) {
        return hasRole(token, UserRole.USER);
    }

    private boolean hasRole(String token, UserRole role) {
        String tokenRole = getUserRole(token);
        return role.name().equals(tokenRole);
    }

    public boolean isValidToken(String token) {
        return jwtUtils.isValidToken(token);
    }

    private String createToken(ClaimRequest dto, long expirationTime) {
        return jwtUtils.createToken(dto, expirationTime);
    }

    private String createAccessToken(ClaimRequest dto) {
        return createToken(dto, expirationProperties.getAccessTokenExpTime());
    }

    private String createRefreshToken(ClaimRequest dto) {
        return createToken(dto, expirationProperties.getRefreshTokenExpTime());
    }

    public TokenResponseDto createTokenResponseDto(UserInfoDto member) {
        ClaimRequest dto = AuthDtoMapper.toClaimRequest(member);
        return new TokenResponseDto(createAccessToken(dto), createRefreshToken(dto));
    }

    public TokenResponseDto regenerateTokens(String refreshToken) {
        ClaimRequest dto = tokenCacheHandler.extractClaimsByToken(refreshToken);
        return new TokenResponseDto(createAccessToken(dto), createRefreshToken(dto));
    }
}
