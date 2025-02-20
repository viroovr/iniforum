package com.forum.project.domain.auth.service;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.auth.dto.*;
import com.forum.project.domain.user.dto.UserCreateDto;
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
    private final TokenService tokenService;
    private final UserPasswordService userPasswordService;
    private final TokenBlacklistHandler tokenBlacklistHandler;
    private final UserService userService;

    public SignupResponseDto createUser(SignupRequestDto dto) {
        UserCreateDto createDto = UserDtoMapper.fromSignupRequestDto(dto, userPasswordService.encode(dto.getPassword()));
        return userService.createUser(createDto);
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public TokenResponseDto loginUserWithTokens(LoginRequestDto dto) {
        User user = userService.findByLoginId(dto.getLoginId());
        userPasswordService.validatePassword(dto.getPassword(), user.getPassword());

        return tokenService.createTokenResponseDto(UserDtoMapper.toUserInfoDto(user));
    }

    private void validateToken(String token) {
        if (!tokenService.isValidToken(token))
            throw new ApplicationException(ErrorCode.AUTH_INVALID_TOKEN,
                    "유효하지 않은 토큰입니다.");
    }

    private void blacklistTokens(TokenRequestDto dto) {
        tokenBlacklistHandler.blacklistRefreshToken(dto.getRefreshToken(), tokenService.getExpirationTime(dto.getRefreshToken()));
        tokenBlacklistHandler.blacklistAccessToken(dto.getAccessToken(), tokenService.getExpirationTime(dto.getAccessToken()));
    }

    @Transactional
    public void logout(TokenRequestDto dto) {
        validateToken(dto.getAccessToken());
        validateToken(dto.getRefreshToken());

        blacklistTokens(dto);
    }

    private void validateBlacklistRefreshToken(String token) {
        if (tokenBlacklistHandler.isBlacklistedRefreshToken(token))
            throw new ApplicationException(ErrorCode.AUTH_BLACKLISTED_REFRESH_TOKEN);
    }

    public TokenResponseDto refreshAccessToken(TokenRequestDto dto) {
        validateToken(dto.getRefreshToken());

        validateBlacklistRefreshToken(dto.getRefreshToken());

        TokenResponseDto tokens = tokenService.regenerateTokens(dto.getRefreshToken());

        blacklistTokens(dto);

        return tokens;
    }
}
