package com.forum.project.application;

import com.forum.project.application.auth.RefreshTokenService;
import com.forum.project.application.security.jwt.JwtBlacklistService;
import com.forum.project.application.security.jwt.JwtTokenProvider;
import com.forum.project.presentation.dtos.token.TokenResponseDto;
import com.forum.project.presentation.dtos.user.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtBlacklistService jwtBlacklistService;
    private final RefreshTokenService refreshTokenService;

    public TokenResponseDto generateTokens(UserInfoDto userInfoDto) {
        String accessToken = jwtTokenProvider.createAccessToken(userInfoDto);
        String refreshToken = jwtTokenProvider.createRefreshToken(userInfoDto);

        ZonedDateTime expiryDate = ZonedDateTime.now().plusDays(7);
        refreshTokenService.saveRefreshToken(refreshToken, userInfoDto.getId(), expiryDate);

        return new TokenResponseDto(accessToken, refreshToken);
    }

    public void invalidTokens(String jwt, String refreshToken, long expirationTime) {
        jwtBlacklistService.addToBlacklist(jwt, "accessToken", expirationTime);
        refreshTokenService.invalidateRefreshToken(refreshToken);
    }
}
