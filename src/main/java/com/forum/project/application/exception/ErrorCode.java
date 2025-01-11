package com.forum.project.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
//  1. Authentication & Authorization
    AUTH_INVALID_PASSWORD("AUTH_001", "Password not match", HttpStatus.CONFLICT),
    AUTH_BLACKLISTED_REFRESH_TOKEN("AUTH_002", "Invalid Refresh Token", HttpStatus.BAD_REQUEST),
    AUTH_INVALID_TOKEN("AUTH_003", "Invalid Token", HttpStatus.BAD_REQUEST),
    AUTH_BLACKLISTED_ACCESS_TOKEN("AUTH_004", "Invalid Access Token", HttpStatus.BAD_REQUEST),
    AUTH_BAD_CREDENTIAL("AUTH_005", "No Access", HttpStatus.BAD_REQUEST),

//  2. Resource Management
    DATABASE_ERROR("INTERNAL_SERVER_ERROR", "Internal Server error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND),
    EMAIL_NOT_FOUND("EMAIL_NOT_FOUND", "Email not found", HttpStatus.NOT_FOUND),
    QUESTION_NOT_FOUND("QUESTION_NOT_FOUND", "Question not found", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "Comment not found", HttpStatus.NOT_FOUND),
    TAG_NOT_FOUND("TAG_NOT_FOUND", "Tag not found", HttpStatus.NOT_FOUND),
    COMMENT_REPORT_NOT_FOUND("COMMENT_REPORT_NOT_FOUND", "CommentReport not found", HttpStatus.NOT_FOUND),
    COMMENT_LIKE_NOT_FOUND("NOT_FOUND_008", "CommentLike not found", HttpStatus.NOT_FOUND),

//  3. Conflict & Duplication
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already exists", HttpStatus.CONFLICT),
    LOGIN_ID_ALREADY_EXISTS("LOGIN_ID_ALREADY_EXISTS", "Login ID already exists", HttpStatus.CONFLICT),
    LIKE_ALREADY_EXISTS("LIKE_ALREADY_EXISTS", "Like already exists", HttpStatus.CONFLICT),
    DISLIKE_ALREADY_EXISTS("DISLIKE_ALREADY_EXISTS", "Dislike already exists", HttpStatus.CONFLICT),
    ACCOUNT_ALREADY_ACTIVE("CONFLICT_004", "Account already active", HttpStatus.CONFLICT),
    USER_ALREADY_ACTIVE("USER_001", "User is already active", HttpStatus.CONFLICT),
    USER_ALREADY_INACTIVE("USER_002", "User is already inactive", HttpStatus.CONFLICT),
    USER_ALREADY_SUSPENDED("USER_003", "User is already suspended", HttpStatus.CONFLICT),
    TAG_ALREADY_EXISTS("DUP_008", "Tag already exists", HttpStatus.CONFLICT),
    COMMENT_ALREADY_DELETED("DUP_009", "Comment already deleted", HttpStatus.CONFLICT),
    COMMENT_ALREADY_REPORTED("DUP_010", "Comment already reported", HttpStatus.CONFLICT),

//  4. Validation
    INVALID_REQUEST("VALID_000", "Invalid Request", HttpStatus.BAD_REQUEST),
    INVALID_USER_ID("VALID_001", "Invalid user ID", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL("VALID_002", "Invalid Email", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD("VALID_003", "Invalid Password", HttpStatus.BAD_REQUEST),
    INVALID_NAME("VALID_004", "Invalid Name", HttpStatus.BAD_REQUEST),
    INVALID_VERIFICATION_CODE("VALID_006", "Invalid Verification Code", HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_TOKEN("VALID_007", "Invalid Refresh Token", HttpStatus.BAD_REQUEST),
    INVALID_AUTH_HEADER("VALID_008", "Invalid Authorization Header", HttpStatus.BAD_REQUEST),
    INVALID_USER_ROLE("VALID_009", "Invalid user role", HttpStatus.BAD_REQUEST),
    INVALID_LOGIN_ID("VALID_010", "Login ID must not be empty", HttpStatus.BAD_REQUEST),
    INVALID_NEW_PASSWORD("VALID_011", "New password must not be empty or too short", HttpStatus.BAD_REQUEST),
    INVALID_COMMENT_CONTENT("VALID_012", "Invalid comment content", HttpStatus.BAD_REQUEST),
    INVALID_COMMENT_REPORT("VALID_013", "Invalid comment report", HttpStatus.BAD_REQUEST),

//   5. Server Errors
    INTERNAL_SERVER_ERROR("SERVER_000", "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    FAIL_SENDING_EMAIL("SERVER_001", "Failed to send Email", HttpStatus.INTERNAL_SERVER_ERROR),

//    Rate Limiting & Resource Limits

//    I/O & Networks
    FAIL_IO("IO_001", "Failed I/O", HttpStatus.INTERNAL_SERVER_ERROR);
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
