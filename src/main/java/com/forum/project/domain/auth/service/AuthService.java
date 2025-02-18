package com.forum.project.domain.auth.service;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.auth.dto.LoginRequestDto;
import com.forum.project.domain.auth.dto.SignupRequestDto;
import com.forum.project.domain.auth.dto.SignupResponseDto;
import com.forum.project.domain.auth.dto.TokenResponseDto;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.user.mapper.UserDtoMapper;
import com.forum.project.domain.user.service.UserService;
import com.forum.project.domain.auth.repository.TokenBlacklistHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserPasswordService userPasswordService;
    private final TokenService tokenService;
    private final TokenBlacklistHandler tokenBlacklistHandler;
    private final UserService userService;

    public SignupResponseDto createUser(SignupRequestDto signupRequestDto) {
        return userService.createUser(signupRequestDto);
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public TokenResponseDto loginUserWithTokens(LoginRequestDto dto) {
        User user = userService.findByLoginId(dto.getLoginId());
        userPasswordService.validatePassword(dto.getPassword(), user.getPassword());

        return tokenService.createTokenResponseDto(UserDtoMapper.toUserInfoDto(user));
    }

    private void validateToken(String token) {
        if (!tokenService.isValidToken(token))
            throw new ApplicationException(ErrorCode.AUTH_INVALID_TOKEN);
    }

    private void blacklistTokens(String refreshToken, String accessToken) {
        tokenBlacklistHandler.blacklistRefreshToken(refreshToken, tokenService.getExpirationTime(refreshToken));
        tokenBlacklistHandler.blacklistAccessToken(accessToken, tokenService.getExpirationTime(accessToken));
    }
    @Transactional
    public void logout(String refreshToken, String accessToken) {
        validateToken(accessToken);
        validateToken(refreshToken);

        blacklistTokens(refreshToken, accessToken);
    }

    private void validateBlacklistToken(String token) {
        if (tokenBlacklistHandler.isBlacklistedRefreshToken(token))
            throw new ApplicationException(ErrorCode.AUTH_BLACKLISTED_REFRESH_TOKEN);
    }

    public TokenResponseDto refreshAccessToken(String refreshToken, String oldAccessToken) {
        validateToken(refreshToken);
        validateBlacklistToken(refreshToken);

        String refreshedAccessToken = tokenService.regenerateAccessToken(refreshToken);
        String newRefreshToken = tokenService.regenerateRefreshToken(refreshToken);

        blacklistTokens(refreshToken, oldAccessToken);

        return new TokenResponseDto(refreshedAccessToken, newRefreshToken);
    }
}
