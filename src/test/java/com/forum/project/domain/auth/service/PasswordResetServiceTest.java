package com.forum.project.domain.auth.service;

import com.forum.project.core.common.ClockUtil;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.auth.dto.PasswordResetRequestDto;
import com.forum.project.domain.auth.entity.ResetToken;
import com.forum.project.domain.auth.repository.ResetTokenRepository;
import com.forum.project.domain.email.service.EmailService;
import com.forum.project.domain.user.service.UserService;
import com.forum.project.presentation.dtos.TestDtoFactory;
import com.forum.project.testUtils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock private UserService userService;
    @Mock private EmailService emailService;
    @Mock private ResetTokenRepository resetTokenRepository;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private static final long TTL = 15;
    private PasswordResetRequestDto passwordResetRequestDto;
    private ResetToken resetToken;

    @BeforeEach
    void setUp() {
        TestUtils.setFixedClock();
        passwordResetRequestDto = TestDtoFactory.createPasswordResetRequestDto();
        resetToken = new ResetToken(passwordResetRequestDto.getToken(), passwordResetRequestDto.getEmail(),
                ClockUtil.now().plusMinutes(TTL));
    }

    @Test
    void resetPassword() {
        when(resetTokenRepository.findByToken(passwordResetRequestDto.getToken())).thenReturn(Optional.of(resetToken));

        assertDoesNotThrow(() -> passwordResetService.resetPassword(passwordResetRequestDto));
    }

    @Test
    void resetPassword_notExists() {
        when(resetTokenRepository.findByToken(passwordResetRequestDto.getToken())).thenReturn(Optional.empty());

        TestUtils.assertApplicationException(
                () -> passwordResetService.resetPassword(passwordResetRequestDto),
                ErrorCode.RESET_TOKEN_NOT_FOUND
        );
    }

    @Test
    void resetPassword_expired() {
        resetToken.setExpiryDate(ClockUtil.now().minusMinutes(TTL));
        when(resetTokenRepository.findByToken(passwordResetRequestDto.getToken())).thenReturn(Optional.of(resetToken));

        TestUtils.assertApplicationException(
                () -> passwordResetService.resetPassword(passwordResetRequestDto),
                ErrorCode.AUTH_INVALID_TOKEN
        );
    }

    @Test
    void sendNewResetTokenToEmail() {
        when(resetTokenRepository.save(any(ResetToken.class))).thenReturn(1);

        passwordResetService.sendNewResetTokenToEmail(passwordResetRequestDto.getEmail());

        verify(resetTokenRepository).save(any(ResetToken.class));
        verify(emailService).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void sendNewResetTokenToEmail_failSave() {
        when(resetTokenRepository.save(any(ResetToken.class))).thenReturn(0);

        TestUtils.assertApplicationException(
                () -> passwordResetService.sendNewResetTokenToEmail(passwordResetRequestDto.getEmail()),
                ErrorCode.DATABASE_ERROR
        );
    }
}