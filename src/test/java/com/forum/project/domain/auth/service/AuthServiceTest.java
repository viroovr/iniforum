package com.forum.project.domain.auth.service;

import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.auth.dto.*;
import com.forum.project.domain.auth.repository.TokenBlacklistHandler;
import com.forum.project.domain.user.dto.UserCreateDto;
import com.forum.project.domain.user.dto.UserInfoDto;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.user.service.UserService;
import com.forum.project.presentation.dtos.TestDtoFactory;
import com.forum.project.testUtils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserPasswordService userPasswordService;
    @Mock
    private TokenService tokenService;
    @Mock
    private TokenBlacklistHandler tokenBlacklistHandler;
    @Mock
    private UserService userService;

    private SignupRequestDto signupRequestDto;
    private SignupResponseDto signupResponseDto;
    private LoginRequestDto loginRequestDto;
    private TokenRequestDto tokenRequestDto;
    private TokenResponseDto tokenResponseDto;
    private UserCreateDto userCreateDto;
    private User mockUser;

    @BeforeEach
    void setUp() {
        signupRequestDto = TestDtoFactory.createSignupRequestDto();
        signupResponseDto = TestDtoFactory.createSignupResponseDto();
        loginRequestDto = TestDtoFactory.createLoginRequestDto();
        tokenRequestDto = TestDtoFactory.createTokenRequestDto();
        tokenResponseDto = TestDtoFactory.createTokenResponseDto();
        userCreateDto = TestDtoFactory.createUserCreateDto();
        mockUser = TestDtoFactory.createUserEntity();
    }

    @Test
    void loginUserWithTokens() {
        when(userService.findByLoginId(loginRequestDto.getLoginId())).thenReturn(mockUser);
        when(tokenService.createTokenResponseDto(any(UserInfoDto.class))).thenReturn(tokenResponseDto);

        TokenResponseDto actual = authService.loginUserWithTokens(loginRequestDto);

        assertThat(actual).isEqualTo(tokenResponseDto);
    }

    @Test
    void createUser() {
        when(userPasswordService.encode(signupRequestDto.getPassword())).thenReturn(userCreateDto.getPassword());
        when(userService.createUser(any(UserCreateDto.class))).thenReturn(signupResponseDto);

        SignupResponseDto actual = authService.createUser(signupRequestDto);

        assertThat(actual).isEqualTo(signupResponseDto);
    }

    private void mockValidateToken(String token, boolean isValidate) {
        when(tokenService.isValidToken(token)).thenReturn(isValidate);
    }

    private void mockBlacklistTokens() {
        when(tokenService.getExpirationTime(tokenRequestDto.getAccessToken())).thenReturn(3600L);
        when(tokenService.getExpirationTime(tokenRequestDto.getRefreshToken())).thenReturn(8600L);
    }

    @Test
    void logout() {
        when(tokenService.isValidToken(tokenRequestDto.getAccessToken())).thenReturn(true);
        when(tokenService.isValidToken(tokenRequestDto.getRefreshToken())).thenReturn(true);
        mockBlacklistTokens();

        authService.logout(tokenRequestDto);
    }

    @Test
    void logout_notValidatedAccessToken() {
        mockValidateToken(tokenRequestDto.getAccessToken(), false);

        TestUtils.assertApplicationException(() -> authService.logout(tokenRequestDto), ErrorCode.AUTH_INVALID_TOKEN);
    }

    @Test
    void logout_notValidatedRefreshToken() {
        mockValidateToken(tokenRequestDto.getAccessToken(), true);
        mockValidateToken(tokenRequestDto.getRefreshToken(), false);

        TestUtils.assertApplicationException(() -> authService.logout(tokenRequestDto), ErrorCode.AUTH_INVALID_TOKEN);
    }

    private void mockValidateBlacklistRefreshToken(String token, boolean isBlacklisted) {
        when(tokenBlacklistHandler.isBlacklistedRefreshToken(token)).thenReturn(isBlacklisted);
    }

    @Test
    void refreshAccessToken() {
        mockValidateToken(tokenRequestDto.getRefreshToken(), true);
        mockValidateBlacklistRefreshToken(tokenRequestDto.getRefreshToken(), false);
        when(tokenService.regenerateTokens(tokenRequestDto.getRefreshToken())).thenReturn(tokenResponseDto);
        mockBlacklistTokens();

        TokenResponseDto actual = authService.refreshAccessToken(tokenRequestDto);

        assertThat(actual).isEqualTo(tokenResponseDto);
    }

    @Test
    void refreshAccessToken_notValidatedRefreshToken() {
        mockValidateToken(tokenRequestDto.getRefreshToken(), false);

        TestUtils.assertApplicationException(() -> authService.refreshAccessToken(tokenRequestDto), ErrorCode.AUTH_INVALID_TOKEN);
    }

    @Test
    void refreshAccessToken_blacklistedRefreshToken() {
        mockValidateToken(tokenRequestDto.getRefreshToken(), true);
        mockValidateBlacklistRefreshToken(tokenRequestDto.getRefreshToken(), true);

        TestUtils.assertApplicationException(() -> authService.refreshAccessToken(tokenRequestDto),
                ErrorCode.AUTH_BLACKLISTED_REFRESH_TOKEN);
    }
}