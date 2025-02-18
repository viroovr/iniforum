package com.forum.project.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.user.vo.UserKey;
import com.forum.project.domain.user.vo.UserRole;
import com.forum.project.domain.user.vo.UserStatus;
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

    @JsonIgnore
    private String password;

    private String lastName;
    private String firstName;
    private String nickname;
    private String profileImagePath;

    @Builder.Default
    private String status = UserStatus.ACTIVE.name();
    @Builder.Default
    private String role = UserRole.USER.name();

    private LocalDateTime lastActivityDate;
    private LocalDateTime lastPasswordModifiedDate;
    private LocalDateTime lastLoginDate;

    private LocalDateTime createdDate;

    public void setKeys(UserKey keys) {
        this.id = keys.getId();
        this.createdDate = keys.getCreatedDate();
        this.lastActivityDate = keys.getLastActivityDate();
        this.lastPasswordModifiedDate = keys.getLastPasswordModifiedDate();
        this.lastLoginDate = keys.getLastLoginDate();
    }

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

    public void updateLastActivityDate() {
        this.lastActivityDate = LocalDateTime.now();
    }

    public void updateLastLoginDate() {
        this.lastLoginDate = LocalDateTime.now();
    }
}