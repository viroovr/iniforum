package com.forum.project.core.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object details;

    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    public ApplicationException(ErrorCode errorCode, Object details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        StackTraceElement[] stackTrace = super.getStackTrace();

        int length = Math.min(3, stackTrace.length);
        StackTraceElement[] shortStackTrace = new StackTraceElement[length];
        System.arraycopy(stackTrace, 0, shortStackTrace, 0, length);
        return shortStackTrace;
    }
}
