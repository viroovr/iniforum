package com.forum.project.application.question;

import org.springframework.data.domain.Sort;

import java.util.Arrays;

public enum QuestionSortType {
    LATEST("created_date", Sort.Direction.DESC),
    OLDEST("created_date", Sort.Direction.ASC),
    UP_VOTES_DESC("up_voted_count", Sort.Direction.DESC),
    UP_VOTES_ASC("up_voted_count", Sort.Direction.ASC),
    DOWN_VOTES_DESC("down_voted_count", Sort.Direction.DESC),
    DOWN_VOTES_ASC("down_voted_count", Sort.Direction.ASC),
    VIEWS_DESC("view_count", Sort.Direction.DESC),
    VIEWS_ASC("view_count", Sort.Direction.ASC);

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