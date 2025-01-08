package com.forum.project.infrastructure.security.auth;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.application.user.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthAspect {

    private final AuthenticationService authenticationService;

    @Before("@annotation(AuthCheck) && args(userId, header)")
    public void checkAuthorizationForUser(AuthCheck authCheck, Long userId, String header) {
        Long jwtUserId = authenticationService.extractUserId(header);

        if (!Objects.equals(userId, jwtUserId)) {
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL);
        }
    }
}
