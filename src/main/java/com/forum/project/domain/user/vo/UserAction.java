package com.forum.project.domain.user.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserAction {
    LOGIN_SUCCESS("User logged in successfully"),
    LOGIN_FAILURE("User login failed"),
    SIGNUP_SUCCESS("User signed up successfully"),
    PASSWORD_CHANGE("User changed password"),
    LOGOUT("User logged out"),
    UPDATE_PROFILE("User updated profile"),
    DELETE_ACCOUNT("User deleted account"),
    UPDATE_ROLE("User updated role"),
    PASSWORD_RESET_REQUEST("User requested password reset"),
    PASSWORD_RESET("User reset password");

    private final String description;
}
