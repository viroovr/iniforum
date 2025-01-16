package com.forum.project.domain.question;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public enum QuestionStatus {
    OPEN,
    CLOSED,
    RESOLVED,
    DELETED;
}