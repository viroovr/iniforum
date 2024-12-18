package com.forum.project.presentation.dtos.user;

import com.forum.project.application.user.UserRole;
import com.forum.project.domain.entity.User;
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
    private Long id;
    private String loginId;
    private String password;
    private String email;
    private String lastName;
    private String firstName;
    private UserRole role;
}
