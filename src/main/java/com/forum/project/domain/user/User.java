package com.forum.project.domain.user;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
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

    public void activate() {
        if ("ACTIVE".equals(this.status)) {
            throw new ApplicationException(ErrorCode.USER_ALREADY_ACTIVE);
        }
        this.status = "ACTIVE";
    }

    public void deactivate() {
        if ("INACTIVE".equals(this.status)) {
            throw new ApplicationException(ErrorCode.USER_ALREADY_INACTIVE);
        }
        this.status = "INACTIVE";
    }

    public void suspend() {
        if ("SUSPENDED".equals(this.status)) {
            throw new ApplicationException(ErrorCode.USER_ALREADY_SUSPENDED);
        }
        this.status = "SUSPENDED";
    }

    // 유효성 검사 로직
    public void validate() {
        if (this.loginId == null || this.loginId.trim().isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_LOGIN_ID);
        }
        if (this.email == null || this.email.trim().isEmpty() || !this.email.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new ApplicationException(ErrorCode.INVALID_EMAIL);
        }
        if (this.password == null || this.password.trim().isEmpty() || this.password.length() < 8) {
            throw new ApplicationException(ErrorCode.INVALID_PASSWORD);
        }
        if (this.role == null || this.role.trim().isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_USER_ROLE);
        }
    }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty() || newPassword.length() < 8) {
            throw new ApplicationException(ErrorCode.INVALID_NEW_PASSWORD);
        }
        this.password = newPassword;
        this.passwordLastModifiedDate = LocalDateTime.now();
    }

    public void updateLastActivityDate() {
        this.lastActivityDate = LocalDateTime.now();
    }

    public void updateLastLoginDate() {
        this.lastLoginDate = LocalDateTime.now();
    }
}