package com.forum.project.core.exception;

import lombok.Getter;

@Getter
public class InfraException extends RuntimeException {

    private final InfraErrorCode errorCode;
    private final Object details;

    public InfraException(InfraErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    public InfraException(InfraErrorCode errorCode, Object details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
    }
}
