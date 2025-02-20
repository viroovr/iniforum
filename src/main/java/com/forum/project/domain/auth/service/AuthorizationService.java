package com.forum.project.domain.auth.service;

import com.forum.project.core.common.TokenUtil;
import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.user.vo.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final TokenService tokenService;

    @Deprecated
    public Long extractUserId(String header) {
        return tokenService.getUserId(TokenUtil.extractToken(header));
    }

    @Deprecated
    public User getUser(Long userId) {
        return new User();
    }

    @Deprecated
    public User extractUserByHeader(String header) {
        return getUser(extractUserId(header));
    }

    public void validateUser(Long userId, String header) {
        Long tokenUserId = tokenService.getUserId(TokenUtil.extractToken(header));
        if (userId.equals(tokenUserId)) return;

        throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL,
                "요청하는 userId와 토큰의 userId가 일치하지 않습니다.");
    }

    public void validateAdminRole(String header) {
        String token = TokenUtil.extractToken(header);
        if (tokenService.isAdmin(token)) return;

        throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL, "No Role");
    }
}
