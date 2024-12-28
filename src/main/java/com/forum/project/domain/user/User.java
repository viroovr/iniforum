package com.forum.project.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Long id;
    private String loginId;
    private String email;
    private String password;
    private String lastName;
    private String firstName;
    private String nickname;
    private String profileImagePath;
    private String status;
    private String role;
    private LocalDateTime lastActivityDate;
    private LocalDateTime passwordLastModifiedDate;
    private LocalDateTime lastLoginDate;
    private LocalDateTime createdDate;
}