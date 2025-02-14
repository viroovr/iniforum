package com.forum.project.domain.like.vo;

import java.util.Arrays;

public enum LikeStatus {
    LIKE,
    DISLIKE,
    NONE;

    public static LikeStatus fromString(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid like status option: " + value));
    }
}
