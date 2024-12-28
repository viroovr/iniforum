package com.forum.project.application.auth;

import com.forum.project.application.converter.UserDtoConverterFactory;
import com.forum.project.application.io.FileService;
import com.forum.project.application.jwt.TokenService;
import com.forum.project.domain.user.User;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.user.UserRepository;
import com.forum.project.domain.user.UserRole;
import com.forum.project.domain.user.UserStatus;
import com.forum.project.presentation.user.UserInfoDto;
import com.forum.project.presentation.user.UserRequestDto;
import com.forum.project.presentation.user.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserPasswordService passwordService;
    private final FileService fileService;

    public UserInfoDto getUserProfileByHeader(String header) {
        String token = tokenService.extractTokenByHeader(header);
        Long userId = tokenService.getId(token);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        return UserDtoConverterFactory.toUserInfoDto(user);
    }

    public UserResponseDto updateUserProfileByHeader(
            String header,
            UserRequestDto userRequestDto,
            MultipartFile multipartFile
    ) throws IOException {
        String token = tokenService.extractTokenByHeader(header);
        Long userId = tokenService.getId(token);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));;

        passwordService.validatePassword(userRequestDto.getPassword(), user.getPassword());

        String newPassword = passwordService.encode(userRequestDto.getNewPassword());
        String uploadDir = fileService.uploadFile(multipartFile);

        user.setPassword(newPassword);
        user.setNickname(userRequestDto.getNickname());
        user.setProfileImagePath(uploadDir);

        User updatedUser = userRepository.update(user);
        return UserDtoConverterFactory.toUserResponseDto(updatedUser);
    }

    public void deactivateInactiveUsers(Duration inactivityPeriod) {
        LocalDateTime thresholdDate = LocalDateTime.now().minus(inactivityPeriod);
        List<User> inactiveUsers = userRepository.findAllByLastActivityDateBefore(thresholdDate);

        for (User user : inactiveUsers) {
            user.setStatus(UserStatus.INACTIVE.name());
        }
        userRepository.updateAll(inactiveUsers);
    }

    public void updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if (!UserRole.isValid(newRole)) {
            throw new ApplicationException(ErrorCode.INVALID_USER_ROLE);
        }

        user.setRole(newRole);
        userRepository.update(user);
    }

//    public void requestPasswordReset(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
//
//        String resetToken = tokenService.generateResetToken(user.getId());
//        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
//    }
//
//    public void resetPassword(String token, String newPassword) {
//        Long userId = tokenService.validateResetToken(token);
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
//
//        String encodedPassword = passwordService.encode(newPassword);
//        user.setPassword(encodedPassword);
//        userRepository.update(user);
//    }
//
//    public void logUserActivity(Long userId, String action) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
//
//        UserActivityLog log = new UserActivityLog(userId, action, LocalDateTime.now());
//        userActivityLogRepository.save(log);
//    }
//
//    public Page<UserInfoDto> searchUsers(String keyword, String role, String status, Pageable pageable) {
//        Page<User> users = userRepository.searchByKeywordAndFilters(keyword, role, status, pageable);
//        return users.map(UserDtoConverterFactory::toUserInfoDto);
//    }
//
//    public void sendProfileUpdateNotification(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
//
//        notificationService.sendNotification(user.getEmail(), "Profile Updated", "Your profile has been successfully updated.");
//    }
//
//    public void recordLoginActivity(Long userId, String ipAddress, String userAgent) {
//        UserLoginLog loginLog = new UserLoginLog(userId, ipAddress, userAgent, LocalDateTime.now());
//        userLoginLogRepository.save(loginLog);
//    }
//
//    public void reactivateAccount(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
//
//        if (!UserStatus.INACTIVE.name().equals(user.getStatus())) {
//            throw new ApplicationException(ErrorCode.ACCOUNT_ALREADY_ACTIVE);
//        }
//
//        user.setStatus(UserStatus.ACTIVE.name());
//        userRepository.update(user);
//    }
}
