package com.forum.project.application.auth;

import com.forum.project.application.converter.UserDtoConverterFactory;
import com.forum.project.application.security.jwt.AccessTokenService;
import com.forum.project.application.security.jwt.RefreshTokenService;
import com.forum.project.application.security.UserPasswordService;
import com.forum.project.application.security.jwt.TokenService;
import com.forum.project.domain.entity.User;
import com.forum.project.domain.exception.*;
import com.forum.project.presentation.dtos.token.TokenResponseDto;
import com.forum.project.presentation.dtos.user.UserInfoDto;
import com.forum.project.presentation.dtos.auth.LoginRequestDto;
import com.forum.project.presentation.dtos.auth.SignupRequestDto;
import com.forum.project.domain.repository.UserRepository;
import com.forum.project.presentation.dtos.auth.SignupResponseDto;
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
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;

    private User prepareUser(SignupRequestDto signupRequestDto) {
        String rawPassword = signupRequestDto.getPassword();
        String encodedPassword = userPasswordService.encode(rawPassword);

        User user = UserDtoConverterFactory.fromSignupRequestDtoToEntity(signupRequestDto);

        user.setPassword(encodedPassword);
        user.setNickname(signupRequestDto.getLoginId());

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
                accessTokenService.createAccessToken(userInfoDto),
                refreshTokenService.createRefreshToken(userInfoDto)
        );
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {
        refreshTokenService.checkTokenValidity(refreshToken);
        accessTokenService.checkTokenValidity(accessToken);

        refreshTokenService.revokeToken(refreshToken);
        accessTokenService.revokeToken(accessToken);
    }

    public TokenResponseDto refreshAccessToken(String refreshToken) {
        refreshTokenService.checkTokenValidity(refreshToken);

        String accessToken = tokenService.regenerateAccessToken(refreshToken);

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
