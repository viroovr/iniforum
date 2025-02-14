package com.forum.project.domain.comment.vo;

import lombok.Getter;

@Getter
public enum CommentStatus {
    ACTIVE,
    INACTIVE,
    DELETED,
    PENDING,
    SPAM;
}
