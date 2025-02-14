package com.forum.project.domain.user.service;

import com.forum.project.domain.auth.service.UserPasswordService;
import com.forum.project.domain.user.infrastructure.FileService;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.user.mapper.UserDtoConverterFactory;
import com.forum.project.domain.user.repository.UserRepository;
import com.forum.project.domain.user.dto.UserRequestDto;
import com.forum.project.domain.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;
    private final UserPasswordService passwordService;
    private final FileService fileService;

    public UserResponseDto updateUserProfile(
            User user,
            UserRequestDto userRequestDto,
            MultipartFile multipartFile
    ) throws IOException {
        passwordService.validatePassword(userRequestDto.getPassword(), user.getPassword());

        String newPassword = passwordService.encode(userRequestDto.getNewPassword());
        String uploadDir = fileService.uploadFile(multipartFile);

        user.setPassword(newPassword);
        user.setNickname(userRequestDto.getNickname());
        user.setProfileImagePath(uploadDir);

        userRepository.updateProfile(user);
        return UserDtoConverterFactory.toUserResponseDto(user);
    }

    public String getLoginId(Long userId) {
        return userRepository.getLoginIdById(userId);
    }
}
