package com.forum.project.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
//  1. Authentication & Authorization
    INVALID_PASSWORD("INVALID_PASSWORD", "Password not match", HttpStatus.BAD_REQUEST),
//  2. Resource Management
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND),

//  3. Conflict & Duplication
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already exists", HttpStatus.CONFLICT),
    USER_ID_ALREADY_EXISTS("USER_ID_ALREADY_EXISTS", "User ID already exists", HttpStatus.CONFLICT);
//     4. Validation
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
