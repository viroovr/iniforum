package com.forum.project.domain.user;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.common.utils.DateUtils;
import com.forum.project.domain.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class User extends BaseEntity {
    private String loginId;
    private String email;
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

    public void setKeys(Map<String, Object> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Keys map cannot be null");
        }

        setId((Long) keys.get(UserKey.ID));
        setCreatedDate(DateUtils.convertToLocalDateTime(keys.get(UserKey.CREATED_DATE)));
        this.lastActivityDate = DateUtils.convertToLocalDateTime(keys.get(UserKey.LAST_ACTIVITY_DATE));
        this.lastPasswordModifiedDate = DateUtils.convertToLocalDateTime(keys.get(UserKey.LAST_PASSWORD_MODIFIED_DATE));
        this.lastLoginDate = DateUtils.convertToLocalDateTime(keys.get(UserKey.LAST_LOGIN_DATE));
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
        this.lastPasswordModifiedDate = LocalDateTime.now();
    }

    public void updateLastActivityDate() {
        this.lastActivityDate = LocalDateTime.now();
    }

    public void updateLastLoginDate() {
        this.lastLoginDate = LocalDateTime.now();
    }
}