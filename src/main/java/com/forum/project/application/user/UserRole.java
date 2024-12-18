package com.forum.project.application.user;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("관리자"),
    USER("일반 사용자"),
    MODERATOR("모더레이터");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }
}