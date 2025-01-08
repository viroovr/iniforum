package com.forum.project.domain.question;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionStatus {
    OPEN("Active"),
    CLOSED("Inactive"),
    RESOLVED("Deleted"),
    DELETED("Deleted");

    private final String status;
}