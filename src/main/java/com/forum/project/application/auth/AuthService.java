package com.forum.project.application.auth;

import com.forum.project.application.ValidationService;
import com.forum.project.application.security.jwt.JwtBlacklistService;
import com.forum.project.application.security.jwt.JwtTokenProvider;
import com.forum.project.domain.entity.User;
import com.forum.project.domain.exception.*;
import com.forum.project.presentation.dtos.user.UserInfoDto;
import com.forum.project.presentation.dtos.auth.LoginRequestDto;
import com.forum.project.presentation.dtos.auth.SignupRequestDto;
import com.forum.project.domain.repository.UserRepository;
import com.forum.project.presentation.dtos.auth.SignupResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuthService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ValidationService validationService;
    private JwtTokenProvider jwtTokenProvider;
    private JwtBlacklistService jwtBlacklistService;
    private RefreshTokenService refreshTokenService;

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

    @Transactional
    public Map<String, String> loginUserWithTokens(LoginRequestDto loginRequestDto) {
        User user = authenticateUser(loginRequestDto);
        return generateTokens(user);
    }

    @Transactional
    public void logout(String token, String refreshToken, long expirationTime) {
        String jwt = jwtTokenProvider.extractTokenByHeader(token);
        jwtBlacklistService.addToBlacklist(jwt, "accessToken", expirationTime);
        refreshTokenService.invalidateRefreshToken(refreshToken);
    }

    private User validateAndPrepareUser(SignupRequestDto signupRequestDto) {
        String rawPassword = signupRequestDto.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = SignupRequestDto.toUser(signupRequestDto);

        user.setPassword(encodedPassword);

        return user;
    }

    private User authenticateUser(LoginRequestDto loginRequestDto) {
        String userId = loginRequestDto.getUserId();
        User user = userRepository.findByUserId(userId);
        checkPassword(loginRequestDto, user);
        return user;
    }

    private void checkPassword(LoginRequestDto loginRequestDto, User user) {
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }
    }

    private Map<String, String> generateTokens(User user) {
        UserInfoDto info = UserInfoDto.toDto(user);
        String accessToken = jwtTokenProvider.createAccessToken(info);
        String refreshToken = jwtTokenProvider.createRefreshToken(info);

        ZonedDateTime expiryDate = ZonedDateTime.now().plusDays(7);
        refreshTokenService.saveRefreshToken(refreshToken, info.getId(), expiryDate);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
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
