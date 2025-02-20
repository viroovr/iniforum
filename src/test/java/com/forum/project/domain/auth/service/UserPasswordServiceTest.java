package com.forum.project.domain.auth.service;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.testUtils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPasswordServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserPasswordService userPasswordService;

    private String rawPassword;
    private String encodedPassword;

    @BeforeEach
    void setUp() {
        rawPassword = "testRawPassword";
        encodedPassword = "testEncodedPassword";
    }

    @Test
    void matches() {
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        boolean result = userPasswordService.matches(rawPassword, encodedPassword);

        assertThat(result).isTrue();
    }

    @Test
    void validatePassword() {
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        assertDoesNotThrow(() -> userPasswordService.validatePassword(rawPassword, encodedPassword));
    }

    @Test
    void validatePassword_notMatch() {
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        TestUtils.assertApplicationException(
                () -> userPasswordService.validatePassword(rawPassword, encodedPassword),
                ErrorCode.AUTH_INVALID_PASSWORD
        );
    }

    @Test
    void encode() {
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        String result = userPasswordService.encode(rawPassword);

        assertThat(result).isEqualTo(encodedPassword);
    }
}