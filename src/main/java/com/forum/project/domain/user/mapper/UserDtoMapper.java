package com.forum.project.domain.user.mapper;

import com.forum.project.domain.user.dto.UserCreateDto;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.auth.dto.SignupRequestDto;
import com.forum.project.domain.auth.dto.SignupResponseDto;
import com.forum.project.domain.user.dto.UserInfoDto;
import com.forum.project.domain.user.dto.UserResponseDto;
import com.forum.project.domain.user.vo.UserKey;
import com.forum.project.domain.user.vo.UserRole;
import com.forum.project.domain.user.vo.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

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

    public static UserCreateDto fromSignupRequestDto(SignupRequestDto dto, String password) {
        return UserCreateDto.fromSignupDto(dto)
                .password(password)
                .status(UserStatus.ACTIVE.name())
                .role(UserRole.USER.name())
                .build();
    }

    public static User toEntity(UserCreateDto dto, UserKey keys) {
        User user = User.builder()
                .loginId(dto.getLoginId())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .lastName(dto.getLastName())
                .firstName(dto.getFirstName())
                .nickname(dto.getNickname())
                .status(dto.getStatus())
                .role(dto.getRole())
                .profileImagePath(dto.getProfileImagePath())
                .build();
        user.setKeys(keys);
        return user;
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
