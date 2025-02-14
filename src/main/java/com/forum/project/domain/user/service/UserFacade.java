package com.forum.project.domain.user.service;

import com.forum.project.domain.auth.service.EmailUserService;
import com.forum.project.domain.auth.service.AuthenticationService;
import com.forum.project.domain.auth.service.PasswordResetService;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.user.dto.UserInfoDto;
import com.forum.project.domain.user.dto.UserRequestDto;
import com.forum.project.domain.user.dto.UserResponseDto;
import com.forum.project.domain.user.mapper.UserDtoConverterFactory;
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
    private final EmailUserService emailUserService;
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
        User user = authenticationService.getUser(userId);
        emailUserService.sendProfileUpdateEmail(user);
    }
}
