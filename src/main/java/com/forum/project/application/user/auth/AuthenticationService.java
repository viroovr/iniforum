package com.forum.project.application.user.auth;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.application.jwt.TokenService;
import com.forum.project.domain.user.User;
import com.forum.project.domain.user.UserRepository;
import com.forum.project.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public Long extractUserId(String header) {
        return tokenService.getUserId(extractTokenByHeader(header));
    }

    public String extractLoginId(String header) {
        return tokenService.getLoginId(
                extractTokenByHeader(header));
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    public User extractUserByHeader(String header) {
        return getUser(extractUserId(header));
    }

    public String extractTokenByHeader(String header) {
        if(header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new ApplicationException(ErrorCode.INVALID_AUTH_HEADER,
                "헤더 값이 null 이거나 헤더가 'Bearer '로 시작하지 않습니다.");
    }

    public void validateUser(Long userId, String header) {
        String token = extractTokenByHeader(header);
        Long tokenUserId = tokenService.getUserId(token);
        if (!userId.equals(tokenUserId))
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL,
                    "요청하는 userId와 토큰의 userId가 일치하지 않습니다.");
    }

    public void validateAdminRole(String header) {
        String token = extractTokenByHeader(header);
        if (!tokenService.hasRole(token, UserRole.ADMIN))
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL, "No Role");
    }
}
