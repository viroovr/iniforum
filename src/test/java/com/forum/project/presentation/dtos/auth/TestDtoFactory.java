package com.forum.project.presentation.dtos.auth;

import com.forum.project.application.user.UserRole;
import com.forum.project.domain.entity.User;
import com.forum.project.presentation.dtos.token.TokenResponseDto;
import com.forum.project.presentation.dtos.user.UserInfoDto;

public class TestDtoFactory {
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

    public static EmailRequestDto createEmailRequestDto() {
        return EmailRequestDto.builder()
                .email("test@test.com")
                .code("testCode")
                .build();
    }

    public static User createUserEntity() {
        return User.builder()
                .loginId("testUser")
                .email("test@test.com")
                .password("testPassword1!")
                .firstName("testFirstName")
                .lastName("testLastName")
                .nickname("testNickName")
                .role(UserRole.USER)
                .build();
    }

    public static UserInfoDto createUserInfoDto() {
        return UserInfoDto.builder()
                .loginId("testUser")
                .email("test@test.com")
                .password("testPassword1!")
                .firstName("testFirstName")
                .lastName("testLastName")
                .role(UserRole.USER)
                .build();
    }
}
