package com.forum.project.domain.comment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentStatus {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    DELETED("삭제됨"),
    PENDING("대기중"),
    SPAM("스팸");

    private final String description;
}
