package com.forum.project.domain.user;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN,
    USER;

    public static boolean isValid (String name) {
        for (UserRole role : UserRole.values()) {
            if (role.name().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}