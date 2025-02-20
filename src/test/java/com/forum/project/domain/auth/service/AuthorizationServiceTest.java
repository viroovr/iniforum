package com.forum.project.domain.auth.service;

import com.forum.project.core.common.LogHelper;
import com.forum.project.core.common.TokenUtil;
import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.user.vo.UserRole;
import com.forum.project.testUtils.TestUtils;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @InjectMocks
    private AuthorizationService authorizationService;

    @Mock
    private TokenService tokenService;

    private final String header = "Bearer accessToken";

    private String extractToken() {
        return TokenUtil.extractToken(header);
    }

    @Test
    void validateUser() {
       when(tokenService.getUserId(extractToken())).thenReturn(1L);

       assertDoesNotThrow(() -> authorizationService.validateUser(1L, header));
    }

    @Test
    void validateUser_notEquals() {
        when(tokenService.getUserId(extractToken())).thenReturn(2L);

        TestUtils.assertApplicationException(
                () -> authorizationService.validateUser(1L, header),
                ErrorCode.AUTH_BAD_CREDENTIAL
        );
    }

    @Test
    void validateAdminRole() {
        when(tokenService.isAdmin(extractToken())).thenReturn(true);

        assertDoesNotThrow(() -> authorizationService.validateAdminRole(header));
    }

    @Test
    void validateAdminRole_notAdmin() {
        when(tokenService.isAdmin(extractToken())).thenReturn(false);

        TestUtils.assertApplicationException(
                () -> authorizationService.validateAdminRole(header),
                ErrorCode.AUTH_BAD_CREDENTIAL
        );
    }
}