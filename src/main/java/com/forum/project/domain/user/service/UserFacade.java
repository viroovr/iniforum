package com.forum.project.domain.user.service;

import com.forum.project.domain.email.service.EmailService;
import com.forum.project.domain.auth.service.AuthorizationService;
import com.forum.project.domain.auth.service.PasswordResetService;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.user.dto.UserInfoDto;
import com.forum.project.domain.user.dto.UserRequestDto;
import com.forum.project.domain.user.dto.UserResponseDto;
import com.forum.project.domain.user.mapper.UserDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final AuthorizationService authorizationService;
    private final UserProfileService userProfileService;
    private final UserManagementService userManagementService;
    private final UserActivityService userActivityService;

    public UserInfoDto getUserProfileByHeader(String header) {
        User user = authorizationService.extractUserByHeader(header);
        return UserDtoMapper.toUserInfoDto(user);
    }

    public UserResponseDto updateUserProfileByHeader(
            String header,
            UserRequestDto userRequestDto,
            MultipartFile multipartFile
    ) throws IOException {
        User user = authorizationService.extractUserByHeader(header);
        return userProfileService.updateUserProfile(user, userRequestDto, multipartFile);
    }

    @Transactional
    public void deactivateInactiveUsers(Duration inactivityPeriod) {
        userManagementService.deactivateInactiveUsers(inactivityPeriod);
    }

    public void logUserActivity(Long userId, String action) {
        userActivityService.logUserActivity(userId, action);
    }

}
