package com.forum.project.domain.exception;

import lombok.Getter;

@Getter
public class CustomDatabaseException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.DATABASE_ERROR;

    public CustomDatabaseException(String message) {
        super(message);
    }
}
