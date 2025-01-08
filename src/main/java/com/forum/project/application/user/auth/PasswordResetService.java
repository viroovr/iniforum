package com.forum.project.application.user.auth;

import com.forum.project.application.user.UserDtoConverterFactory;
import com.forum.project.application.email.EmailService;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.application.jwt.TokenService;
import com.forum.project.domain.user.User;
import com.forum.project.infrastructure.persistence.user.UserRepository;
import com.forum.project.presentation.user.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final UserPasswordService passwordService;

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        UserInfoDto userInfoDto = UserDtoConverterFactory.toUserInfoDto(user);

        String resetToken = tokenService.createPasswordResetToken(userInfoDto);
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }

    public void resetPassword(String token, String newPassword) {
        if (token == null || !tokenService.isValidToken(token)) {
            throw new ApplicationException(ErrorCode.AUTH_INVALID_TOKEN);
        }
        Long userId = tokenService.getId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        String encodedPassword = passwordService.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.update(user);
    }
}
