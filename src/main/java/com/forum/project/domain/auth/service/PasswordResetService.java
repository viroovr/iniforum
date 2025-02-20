package com.forum.project.domain.auth.service;

import com.forum.project.core.common.ClockUtil;
import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.auth.dto.PasswordResetRequestDto;
import com.forum.project.domain.auth.entity.ResetToken;
import com.forum.project.domain.auth.repository.ResetTokenRepository;
import com.forum.project.domain.email.service.EmailService;
import com.forum.project.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserService userService;
    private final EmailService emailService;
    private final ResetTokenRepository resetTokenRepository;
    private final UserPasswordService userPasswordService;

    private static final long TTL = 15;

    private void deleteResetToken(String token) {
        resetTokenRepository.deleteByToken(token);
    }

    private void validateExpiryDate(ResetToken resetToken) {
        if (resetToken.isExpired()){
            deleteResetToken(resetToken.getToken());
            throw new ApplicationException(ErrorCode.AUTH_INVALID_TOKEN,
                    "리셋 토큰의 유효시간이 지났습니다.");
        }
    }

    private ResetToken getExistentResetToken(String token) {
        return resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESET_TOKEN_NOT_FOUND,
                        "존재하지 않는 리셋 토큰입니다."));
    }

    private void validateResetToken(String token) {
        ResetToken resetToken = getExistentResetToken(token);

        validateExpiryDate(resetToken);
    }

    private ResetToken createResetToken(String email) {
        String token = UUID.randomUUID().toString();
        return new ResetToken(token, email, ClockUtil.now().plusMinutes(TTL));
    }

    @Transactional
    private void saveResetToken(ResetToken resetToken) {
        int updated = resetTokenRepository.save(resetToken);

        if (updated == 0) throw new ApplicationException(ErrorCode.DATABASE_ERROR,
                "리셋 토큰을 저장하지 못했습니다.");
    }

    public void sendNewResetTokenToEmail(String email) {
        ResetToken resetToken = createResetToken(email);

        saveResetToken(resetToken);
        emailService.sendPasswordResetEmail(email, resetToken.getToken());
    }

    @Transactional
    public void resetPassword(PasswordResetRequestDto dto) {
        validateResetToken(dto.getToken());

        dto.setNewPassword(userPasswordService.encode(dto.getNewPassword()));
        userService.updatePassword(dto);

        deleteResetToken(dto.getToken());
    }
}
