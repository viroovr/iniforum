package com.forum.project.application.user;

import com.forum.project.application.converter.UserDtoConverter;
import com.forum.project.application.security.UserPasswordService;
import com.forum.project.application.security.jwt.TokenService;
import com.forum.project.domain.entity.User;
import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.ErrorCode;
import com.forum.project.domain.repository.UserRepository;
import com.forum.project.presentation.dtos.user.UserInfoDto;
import com.forum.project.presentation.dtos.user.UserRequestDto;
import com.forum.project.presentation.dtos.user.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserPasswordService passwordService;
    private final UserDtoConverter userDtoConverter;

    public UserInfoDto getUserProfile(String token) {
        Long id = tokenService.getId(token);
        User user = userRepository.findById(id).orElseThrow(
                () -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        return UserInfoDto.toDto(user);
    }

    public UserResponseDto updateUserProfile(String token, UserRequestDto userRequestDto, String uploadDir) throws IOException {
        Long id = tokenService.getId(token);
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));;
        passwordService.validatePassword(userRequestDto.getPassword(), user.getPassword());

        String newPassword = passwordService.encode(userRequestDto.getNewPassword());

        user.setPassword(newPassword);
        user.setNickname(userRequestDto.getNickname());
        user.setProfileImagePath(uploadDir);

        return userDtoConverter.toUserResponseDto(userRepository.update(user));
    }
}
