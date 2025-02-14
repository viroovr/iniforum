package com.forum.project.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoDto {
    private Long userId;
    private String loginId;
    private String password;
    private String email;
    private String lastName;
    private String firstName;
    private String nickname;
    private String role;
    private String status;
    private LocalDateTime passwordLastModifiedDate;
}
