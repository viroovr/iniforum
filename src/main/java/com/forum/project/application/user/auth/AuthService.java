package com.forum.project.application.user.auth;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.application.jwt.TokenService;
import com.forum.project.application.user.UserDtoConverterFactory;
import com.forum.project.application.user.UserFacade;
import com.forum.project.domain.user.User;
import com.forum.project.domain.user.UserAction;
import com.forum.project.domain.user.UserRole;
import com.forum.project.domain.user.UserStatus;
import com.forum.project.infrastructure.jwt.AccessRedisTokenBlacklistHandler;
import com.forum.project.infrastructure.jwt.RefreshRedisTokenBlacklistHandler;
import com.forum.project.domain.user.UserRepository;
import com.forum.project.presentation.auth.LoginRequestDto;
import com.forum.project.presentation.auth.SignupRequestDto;
import com.forum.project.presentation.auth.SignupResponseDto;
import com.forum.project.presentation.dtos.TokenResponseDto;
import com.forum.project.presentation.user.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserPasswordService userPasswordService;
    private final TokenService tokenService;
    private final RefreshRedisTokenBlacklistHandler refreshTokenBlacklistService;
    private final AccessRedisTokenBlacklistHandler accessTokenBlacklistService;
    private final AuthenticationService authenticationService;
    private final UserFacade userFacade;

    private User prepareUser(SignupRequestDto signupRequestDto) {
        String rawPassword = signupRequestDto.getPassword();
        String encodedPassword = userPasswordService.encode(rawPassword);

        User user = UserDtoConverterFactory.fromSignupRequestDtoToEntity(signupRequestDto);

        user.setPassword(encodedPassword);
        user.setNickname(signupRequestDto.getLoginId());
        user.setRole(UserRole.USER.name());
        user.setStatus(UserStatus.ACTIVE.name());

        return user;
    }

    @Transactional
    public SignupResponseDto createUser(SignupRequestDto signupRequestDto) {
        if (userRepository.emailExists(signupRequestDto.getEmail())) {
            throw new ApplicationException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.userLoginIdExists(signupRequestDto.getLoginId())) {
            throw new ApplicationException(ErrorCode.LOGIN_ID_ALREADY_EXISTS);
        }

        User preparedUser = prepareUser(signupRequestDto);

        User committedUser = userRepository.save(preparedUser);

        userFacade.logUserActivity(committedUser.getId(), UserAction.SIGNUP_SUCCESS.name());
        return UserDtoConverterFactory.toSignupResponseDto(committedUser);
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public TokenResponseDto loginUserWithTokens(LoginRequestDto loginRequestDto) {
        String loginId = loginRequestDto.getLoginId();
        String password = loginRequestDto.getPassword();

        User user = userRepository.findByUserLoginId(loginId)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        userPasswordService.validatePassword(password, user.getPassword());

        UserInfoDto userInfoDto = UserDtoConverterFactory.toUserInfoDto(user);

        return new TokenResponseDto(
                tokenService.createAccessToken(userInfoDto),
                tokenService.createRefreshToken(userInfoDto)
        );
    }

    @Transactional
    public void logout(String refreshToken, String header) {
        String accessToken = authenticationService.extractTokenByHeader(header);
        if (refreshToken == null || !tokenService.isValidToken(accessToken)) {
            throw new ApplicationException(ErrorCode.AUTH_INVALID_TOKEN);
        }
        long refreshTokenTtl = tokenService.getExpirationTime(refreshToken);
        long accessTokenTtl = tokenService.getExpirationTime(accessToken);
        refreshTokenBlacklistService.blacklistToken(refreshToken, refreshTokenTtl);
        accessTokenBlacklistService.blacklistToken(accessToken, accessTokenTtl);
    }

    public TokenResponseDto refreshAccessToken(String refreshToken, String header) {
        if (refreshTokenBlacklistService.isBlacklistedToken(refreshToken)) {
            throw new ApplicationException(ErrorCode.AUTH_BLACKLISTED_REFRESH_TOKEN);
        }

        if (!tokenService.isValidToken(refreshToken)) {
            throw new ApplicationException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        String oldAccessToken = authenticationService.extractTokenByHeader(header);
        String refreshedAccessToken = tokenService.regenerateAccessToken(refreshToken);
        String newRefreshToken = tokenService.regenerateRefreshToken(refreshToken);
        refreshTokenBlacklistService.blacklistToken(refreshToken, tokenService.getExpirationTime(refreshToken));
        accessTokenBlacklistService.blacklistToken(oldAccessToken, tokenService.getExpirationTime(oldAccessToken));

        return new TokenResponseDto(
                refreshedAccessToken,
                newRefreshToken
        );
    }
}
