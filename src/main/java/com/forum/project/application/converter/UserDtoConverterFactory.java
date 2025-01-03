package com.forum.project.application.converter;

import com.forum.project.domain.user.User;
import com.forum.project.presentation.auth.SignupRequestDto;
import com.forum.project.presentation.auth.SignupResponseDto;
import com.forum.project.presentation.user.UserInfoDto;
import com.forum.project.presentation.user.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserDtoConverterFactory {

    public static UserResponseDto toUserResponseDto(User user) {
        return UserResponseDto.builder()
                .profileImagePath(user.getProfileImagePath())
                .nickname(user.getNickname())
                .build();
    }

    public static User fromSignupRequestDtoToEntity(SignupRequestDto signupRequestDto) {
        return User.builder()
                .loginId(signupRequestDto.getLoginId())
                .email(signupRequestDto.getEmail())
                .password(signupRequestDto.getPassword())
                .firstName(signupRequestDto.getFirstName())
                .lastName(signupRequestDto.getLastName())
                .build();
    }

    public static SignupRequestDto toSignupRequestDto(User user) {
        return SignupRequestDto.builder()
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public static SignupResponseDto toSignupResponseDto(User user) {
        return SignupResponseDto.builder()
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public static UserInfoDto toUserInfoDto(User user) {
        return UserInfoDto.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .password(user.getPassword())
                .email(user.getEmail())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .role(user.getRole())
                .status(user.getStatus())
                .passwordLastModifiedDate(user.getPasswordLastModifiedDate())
                .build();
    }
}
