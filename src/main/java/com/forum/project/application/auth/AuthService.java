package com.forum.project.application.auth;

import com.forum.project.application.RefreshTokenService;
import com.forum.project.application.ValidationService;
import com.forum.project.application.security.jwt.JwtBlacklistService;
import com.forum.project.application.security.jwt.JwtTokenProvider;
import com.forum.project.domain.User;
import com.forum.project.domain.exception.InvalidPasswordException;
import com.forum.project.presentation.auth.CustomUserInfoDto;
import com.forum.project.presentation.auth.LoginRequestDto;
import com.forum.project.presentation.auth.SignupRequestDto;
import com.forum.project.domain.UserRepository;
import com.forum.project.presentation.auth.SignupResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        User user = validateAndPrepareUser(signupRequestDto);
        user = userRepository.save(user);
        return SignupResponseDto.toDto(user);
    }

    @Transactional
    public Map<String, String> loginUserWithTokens(LoginRequestDto loginRequestDto) {
        User user = authenticateUser(loginRequestDto);
        return generateTokens(user);
    }

    @Transactional
    public void logout(String jwt, String refreshToken, long expirationTime) {
        jwtBlacklistService.addToBlacklist(jwt, "accessToken", expirationTime);
        refreshTokenService.invalidateRefreshToken(refreshToken);
    }

    private User validateAndPrepareUser(SignupRequestDto signupRequestDto) {
        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
        User user = SignupRequestDto.toUser(signupRequestDto);
        user.setPassword(encodedPassword);
        validationService.validate(user);
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
        CustomUserInfoDto info = CustomUserInfoDto.toDto(user);
        String accessToken = jwtTokenProvider.createAccessToken(info);
        String refreshToken = jwtTokenProvider.createRefreshToken(info);

        ZonedDateTime expiryDate = ZonedDateTime.now().plusDays(7);
        refreshTokenService.saveRefreshToken(refreshToken, info.getId(), expiryDate);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

}
