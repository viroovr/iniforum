package com.forum.project.presentation.dtos;

import com.forum.project.domain.auth.dto.*;
import com.forum.project.domain.bookmark.dto.BookmarkRequestDto;
import com.forum.project.domain.bookmark.entity.Bookmark;
import com.forum.project.domain.report.dto.ReportRequestDto;
import com.forum.project.domain.report.vo.ReportReason;
import com.forum.project.domain.user.dto.UserCreateDto;
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

    public static TokenRequestDto createTokenRequestDto() {
        return TokenRequestDto.builder()
                .refreshToken("refreshToken")
                .accessToken("accessToken")
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
                .refreshToken("refreshToken")
                .accessToken("accessToken")
                .build();
    }

    public static UserCreateDto createUserCreateDto() {
        return UserCreateDto.builder()
                .loginId("testId")
                .password("testPassword1!")
                .email("test@test.com")
                .lastName("testLastName")
                .firstName("testFirstName")
                .nickname("testNickname")
                .status(UserStatus.ACTIVE.name())
                .role(UserRole.USER.name())
                .profileImagePath("test/path")
                .build();
    }

    public static BookmarkRequestDto createBookmarkRequestDto() {
        return BookmarkRequestDto.builder()
                .userId(1L)
                .questionId(1L)
                .notes("testNotes")
                .build();
    }

    public static Bookmark createBookmark() {
        return Bookmark.builder()
                .id(1L)
                .userId(1L)
                .questionId(1L)
                .notes("testNotes")
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

    public static ReportRequestDto createReportRequestDto() {
        return ReportRequestDto.builder()
                .reason(ReportReason.SPAM)
                .details("testDetails")
                .build();
    }

    public static PasswordResetRequestDto createPasswordResetRequestDto() {
        return PasswordResetRequestDto.builder()
                .token("testToken")
                .email("test@test.com")
                .newPassword("newPassword")
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
