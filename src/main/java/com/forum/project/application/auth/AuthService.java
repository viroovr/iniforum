package com.forum.project.application.auth;

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

    private final RefreshTokenService refreshTokenService;

    private final AccessTokenService accessTokenService;

    private final TokenService tokenService;

    private User prepareUser(SignupRequestDto signupRequestDto) {
        String rawPassword = signupRequestDto.getPassword();
        String encodedPassword = userPasswordService.encode(rawPassword);

        User user = SignupRequestDto.toUser(signupRequestDto);

        user.setPassword(encodedPassword);
        user.setNickname(signupRequestDto.getUserId());

        return user;
    }

    @Transactional
    public SignupResponseDto createUser(SignupRequestDto signupRequestDto) {

        if (userRepository.emailExists(signupRequestDto.getEmail())) {
            throw new ApplicationException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.userIdExists(signupRequestDto.getUserId())) {
            throw new ApplicationException(ErrorCode.USER_ID_ALREADY_EXISTS);
        }

        User preparedUser = prepareUser(signupRequestDto);

        User committedUser = userRepository.save(preparedUser);

        return SignupResponseDto.toDto(committedUser);
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public TokenResponseDto loginUserWithTokens(LoginRequestDto loginRequestDto) {
        String userId = loginRequestDto.getUserId();
        String password = loginRequestDto.getPassword();

        User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        UserInfoDto userInfoDto = UserInfoDto.toDto(user);

        userPasswordService.validatePassword(password, userInfoDto.getPassword());

        return new TokenResponseDto(
                accessTokenService.createAccessToken(userInfoDto),
                refreshTokenService.createRefreshToken(userInfoDto)
        );
    }

    @Transactional
    public void logout(String authHeader, String refreshToken) {
        String accessToken = accessTokenService.extractTokenByHeader(authHeader);

        refreshTokenService.validateToken(refreshToken);
        accessTokenService.validateToken(accessToken);

        accessTokenService.invalidateToken(accessToken);
        refreshTokenService.invalidateToken(refreshToken);
    }

    public TokenResponseDto refreshAccessToken(String refreshToken) {
        refreshTokenService.validateToken(refreshToken);

        String accessToken = tokenService.regenerateAccessToken(refreshToken);

        return new TokenResponseDto(
                accessToken,
                null
        );
    }
}
