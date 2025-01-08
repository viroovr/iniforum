package com.forum.project.application.user.auth;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.application.jwt.TokenService;
import com.forum.project.domain.user.User;
import com.forum.project.infrastructure.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public Long extractUserId(String header) {
        return tokenService.getId(tokenService.extractTokenByHeader(header));
    }

    public String extractLoginId(String header) {
        return tokenService.getLoginId(
                tokenService.extractTokenByHeader(header));
    }

    public User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    public User extractUserByHeader(String header) {
        return validateUser(extractUserId(header));
    }
}
