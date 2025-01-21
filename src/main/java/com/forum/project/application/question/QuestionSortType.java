package com.forum.project.application.question;

import org.springframework.data.domain.Sort;

import java.util.Arrays;

public enum QuestionSortType {
    LATEST("createdDate", Sort.Direction.DESC),
    OLDEST("createdDate", Sort.Direction.ASC),
    VOTES("upvoteCount", Sort.Direction.DESC),
    VIEWS("viewCount", Sort.Direction.DESC);

    private final String property;
    private final Sort.Direction direction;

    QuestionSortType(String property, Sort.Direction direction) {
        this.property = property;
        this.direction = direction;
    }

    public Sort getSort() {
        return Sort.by(direction, property);
    }

    public static QuestionSortType fromString(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid sort option: " + value));
    }
}