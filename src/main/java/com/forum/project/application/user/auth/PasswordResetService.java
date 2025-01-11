package com.forum.project.application.user.auth;

import com.forum.project.application.email.EmailUserService;
import com.forum.project.application.user.UserDtoConverterFactory;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.application.jwt.TokenService;
import com.forum.project.domain.user.User;
import com.forum.project.domain.user.UserRepository;
import com.forum.project.presentation.user.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final EmailUserService emailUserService;
    private final UserPasswordService passwordService;

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        UserInfoDto userInfoDto = UserDtoConverterFactory.toUserInfoDto(user);

        String resetToken = tokenService.createPasswordResetToken(userInfoDto);
        emailUserService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }

    public void resetPassword(String token, String newPassword) {
        if (token == null || !tokenService.isValidToken(token)) {
            throw new ApplicationException(ErrorCode.AUTH_INVALID_TOKEN);
        }
        Long userId = tokenService.getUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        String encodedPassword = passwordService.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.update(user);
    }
}
