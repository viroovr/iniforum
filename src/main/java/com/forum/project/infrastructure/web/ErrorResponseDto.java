package com.forum.project.infrastructure.web;

import com.forum.project.core.exception.ErrorCode;
import com.forum.project.core.exception.InfraErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;

    public ErrorResponseDto(ErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.errorCode = errorCode.getCode();
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDto(InfraErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.errorCode = errorCode.getCode();
        this.timestamp = LocalDateTime.now();
    }
}
