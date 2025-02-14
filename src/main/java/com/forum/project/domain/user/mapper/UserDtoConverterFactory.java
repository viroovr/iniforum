package com.forum.project.domain.user.mapper;

import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.auth.dto.SignupRequestDto;
import com.forum.project.domain.auth.dto.SignupResponseDto;
import com.forum.project.domain.user.dto.UserInfoDto;
import com.forum.project.domain.user.dto.UserResponseDto;
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
                .userId(user.getId())
                .loginId(user.getLoginId())
                .password(user.getPassword())
                .email(user.getEmail())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .role(user.getRole())
                .status(user.getStatus())
                .nickname(user.getNickname())
                .passwordLastModifiedDate(user.getLastPasswordModifiedDate())
                .build();
    }
}
