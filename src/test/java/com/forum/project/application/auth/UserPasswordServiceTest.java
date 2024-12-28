package com.forum.project.application.auth;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testMatches() {
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        assertTrue(userPasswordService.matches(rawPassword, encodedPassword));
    }

    @Test
    void testValidatePassword_Success() {
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        assertDoesNotThrow(() ->
                userPasswordService.validatePassword(rawPassword, encodedPassword));
    }

    @Test
    void testValidatePassword_ThrowAuthInvalidException() {
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        ApplicationException applicationException = assertThrows(ApplicationException.class, () ->
                userPasswordService.validatePassword(rawPassword, encodedPassword));

        assertEquals(applicationException.getErrorCode(), ErrorCode.AUTH_INVALID_PASSWORD);
    }

    @Test
    void testEncode() {
        String expectedEncodedPassword = "encodedPassword";
        when(passwordEncoder.encode(rawPassword)).thenReturn(expectedEncodedPassword);

        String actualEncodedPassword = userPasswordService.encode(rawPassword);

        assertEquals(expectedEncodedPassword, actualEncodedPassword);
    }
}