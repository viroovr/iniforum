package com.forum.project.application.user;

import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.application.user.auth.PasswordResetService;
import com.forum.project.domain.user.User;
import com.forum.project.presentation.user.UserInfoDto;
import com.forum.project.presentation.user.UserRequestDto;
import com.forum.project.presentation.user.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final AuthenticationService authenticationService;
    private final UserProfileService userProfileService;
    private final UserNotificationService userNotificationService;
    private final UserManagementService userManagementService;
    private final PasswordResetService passwordResetService;
    private final UserActivityService userActivityService;


    public UserInfoDto getUserProfileByHeader(String header) {
        User user = authenticationService.extractUserByHeader(header);
        return UserDtoConverterFactory.toUserInfoDto(user);
    }

    public UserResponseDto updateUserProfileByHeader(
            String header,
            UserRequestDto userRequestDto,
            MultipartFile multipartFile
    ) throws IOException {
        User user = authenticationService.extractUserByHeader(header);
        return userProfileService.updateUserProfile(user, userRequestDto, multipartFile);
    }

    @Transactional
    public void deactivateInactiveUsers(Duration inactivityPeriod) {
        userManagementService.deactivateInactiveUsers(inactivityPeriod);
    }

    public void requestPasswordReset(String email) {
        passwordResetService.requestPasswordReset(email);
    }

    public void resetPassword(String token, String newPassword) {
        passwordResetService.resetPassword(token, newPassword);
    }

    public void logUserActivity(Long userId, String action) {
        userActivityService.logUserActivity(userId, action);
    }

    public void sendProfileUpdateNotification(Long userId) {
        User user = authenticationService.validateUser(userId);
        userNotificationService.sendProfileUpdateNotification(user);
    }
}
