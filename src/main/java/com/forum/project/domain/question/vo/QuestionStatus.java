package com.forum.project.domain.question.vo;

import java.util.Arrays;

public enum QuestionStatus {
    OPEN,
    CLOSED,
    RESOLVED,
    DELETED;

    public static QuestionStatus fromString(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status option: " + value));
    }
}