package com.forum.project.application.user;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE("활성화된 사용자"),
    INACTIVE("비활성화된 사용자"),
    SUSPENDED("정지된 사용자"),
    DELETED("삭제된 사용자");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }
}