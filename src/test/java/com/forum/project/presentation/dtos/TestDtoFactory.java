package com.forum.project.presentation.dtos;

import com.forum.project.domain.auth.dto.*;
import com.forum.project.domain.user.vo.UserRole;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.user.vo.UserStatus;
import com.forum.project.domain.user.dto.UserInfoDto;

import java.time.LocalDateTime;

public class TestDtoFactory {
    private static final LocalDateTime DATETIME =
            LocalDateTime.of(
                    2024,12,12,12,12,12
            );

    public static SignupRequestDto createSignupRequestDto() {
        return SignupRequestDto.builder()
                .loginId("testId")
                .email("test@test.com")
                .password("testPassword1!")
                .lastName("testLastName")
                .firstName("testFirstName")
                .build();
    }

    public static SignupResponseDto createSignupResponseDto() {
        return SignupResponseDto.builder()
                .loginId("testId")
                .email("test@test.com")
                .lastName("testLastName")
                .firstName("testFirstName")
                .build();
    }

    public static LoginRequestDto createLoginRequestDto() {
        return LoginRequestDto.builder()
                .loginId("testId")
                .password("testPassword1!")
                .build();
    }

    public static TokenResponseDto createTokenResponseDto() {
        return TokenResponseDto.builder()
                .refreshToken("refresh-token")
                .accessToken("access-token")
                .build();
    }

    public static EmailVerificationRequestDto createEmailVerificationRequestDto() {
        return EmailVerificationRequestDto.builder()
                .email("test@test.com")
                .build();
    }

    public static EmailVerificationConfirmDto createEmailVerificationConfirmDto() {
        return EmailVerificationConfirmDto.builder()
                .email("test@test.com")
                .code("123456")
                .build();
    }

    public static EmailRequestDto createEmailRequestDto() {
        return EmailRequestDto.builder()
                .email("test@test.com")
                .code("testCode")
                .build();
    }

    public static User createUserEntity() {
        return User.builder()
                .id(1L)
                .loginId("testId")
                .email("test@test.com")
                .password("testPassword1!")
                .firstName("testFirstName")
                .lastName("testLastName")
                .nickname("testNickName")
                .role(UserRole.USER.name())
                .status(UserStatus.ACTIVE.name())
                .lastPasswordModifiedDate(DATETIME)
                .build();
    }

    public static UserInfoDto createUserInfoDto() {
        return UserInfoDto.builder()
                .userId(1L)
                .loginId("testUser")
                .email("test@test.com")
                .password("testPassword1!")
                .firstName("testFirstName")
                .lastName("testLastName")
                .role(UserRole.USER.name())
                .status(UserStatus.ACTIVE.name())
                .passwordLastModifiedDate(DATETIME)
                .build();
    }
}
