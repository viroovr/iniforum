package com.forum.project.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum InfraErrorCode {

    FAIL_ACCESS("REPO_001", "Failed to access data", HttpStatus.INTERNAL_SERVER_ERROR),
    //    Rate Limiting & Resource Limits
    FAIL_SENDING_EMAIL("SERVER_001", "Failed to send Email", HttpStatus.INTERNAL_SERVER_ERROR),

    //    I/O & Networks
    FAIL_IO("IO_001", "Failed I/O", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    InfraErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
