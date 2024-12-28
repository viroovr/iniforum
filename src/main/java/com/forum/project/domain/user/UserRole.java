package com.forum.project.domain.user;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ADMIN"),
    USER("USER");

    private final String name;

    UserRole(String name) {
        this.name = name;
    }

    public static boolean isValid (String name) {
        for (UserRole role : UserRole.values()) {
            if (role.name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}