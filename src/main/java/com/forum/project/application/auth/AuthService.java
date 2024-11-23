package com.forum.project.application.auth;

import com.forum.project.application.TokenService;
import com.forum.project.application.security.PasswordUtil;
import com.forum.project.application.security.jwt.JwtTokenProvider;
import com.forum.project.domain.entity.User;
import com.forum.project.domain.exception.*;
import com.forum.project.presentation.dtos.token.TokenResponseDto;
import com.forum.project.presentation.dtos.user.UserInfoDto;
import com.forum.project.presentation.dtos.auth.LoginRequestDto;
import com.forum.project.presentation.dtos.auth.SignupRequestDto;
import com.forum.project.domain.repository.UserRepository;
import com.forum.project.presentation.dtos.auth.SignupResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuthService {

    private UserRepository userRepository;
    private PasswordUtil passwordUtil;
    private RefreshTokenService refreshTokenService;
    private JwtTokenProvider jwtTokenProvider;
    private TokenService tokenService;


    private User validateAndPrepareUser(SignupRequestDto signupRequestDto) {
        String rawPassword = signupRequestDto.getPassword();
        String encodedPassword = passwordUtil.encode(rawPassword);

        User user = SignupRequestDto.toUser(signupRequestDto);

        user.setPassword(encodedPassword);

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


        User validatedUser = validateAndPrepareUser(signupRequestDto);
        validatedUser.setNickname(signupRequestDto.getUserId());

        User committedUser = userRepository.save(validatedUser);

        return SignupResponseDto.toDto(committedUser);
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public TokenResponseDto loginUserWithTokens(LoginRequestDto loginRequestDto) {
        String userId = loginRequestDto.getUserId();
        String password = loginRequestDto.getPassword();

        User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        UserInfoDto userInfoDto = UserInfoDto.toDto(user);

        if (!passwordUtil.matches(password, userInfoDto.getPassword())) {
            throw new ApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        return tokenService.generateTokens(userInfoDto);
    }

    @Transactional
    public void logout(String token, String refreshToken, long expirationTime) {
        String jwt = jwtTokenProvider.extractTokenByHeader(token);
        tokenService.invalidTokens(jwt, refreshToken, expirationTime);
    }

    public long getJwtExpirationTime(String token) {
        String jwt = jwtTokenProvider.extractTokenByHeader(token);
        return jwtTokenProvider.getExpirationTime(jwt);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return jwtTokenProvider.validateToken(refreshToken);
    }

    public String refreshAccessToken(String refreshToken) {
        refreshTokenService.validateRefreshToken(refreshToken);
        return jwtTokenProvider.regenerateAccessToken(refreshToken);
    }


}
