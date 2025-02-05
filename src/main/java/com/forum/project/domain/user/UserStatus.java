package com.forum.project.domain.user;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    LOCKED,
    SUSPENDED,
    DELETED;

    public static UserStatus fromString(String name) {
        for (UserStatus status : UserStatus.values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No status found with name: " + name);
    }
}