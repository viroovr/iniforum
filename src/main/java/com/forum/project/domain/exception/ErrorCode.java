package com.forum.project.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
//  1. Authentication & Authorization
    AUTH_INVALID_PASSWORD("AUTH_001", "Password not match", HttpStatus.CONFLICT),
    AUTH_BLACKLISTED_REFRESH_TOKEN("AUTH_002", "Invalid Refresh Token", HttpStatus.BAD_REQUEST),
    AUTH_INVALID_TOKEN("AUTH_003", "Invalid Token", HttpStatus.BAD_REQUEST),
    AUTH_BLACKLISTED_ACCESS_TOKEN("AUTH_004", "Invalid Access Token", HttpStatus.BAD_REQUEST),
//  2. Resource Management
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND),

//  3. Conflict & Duplication
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already exists", HttpStatus.CONFLICT),
    USER_ID_ALREADY_EXISTS("USER_ID_ALREADY_EXISTS", "User ID already exists", HttpStatus.CONFLICT),
//  4. Validation
    VALIDATION_ERROR("VALIDATION_ERROR", "Invalid arguments", HttpStatus.BAD_REQUEST),
    INVALID_USER_ID("VALID_001", "Invalid user ID", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL("VALID_002", "Invalid Email", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD("VALID_003", "Invalid Password", HttpStatus.BAD_REQUEST),
    INVALID_NAME("VALID_004", "Invalid Name", HttpStatus.BAD_REQUEST);
//   5. Server Errors
//    Rate Limiting & Resource Limits
//    I/O & Networks
//

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
