package com.forum.project.infrastructure.persistence;

import com.forum.project.application.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CustomDatabaseException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.DATABASE_ERROR;

    public CustomDatabaseException(String message) {
        super(message);
    }
}
