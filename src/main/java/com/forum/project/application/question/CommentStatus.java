package com.forum.project.application.question;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentStatus {
    ACTIVE("활성"),
    DELETED("삭제됨"),
    PENDING("대기중");

    private final String description;
}
