package com.forum.project.presentation.error;

import com.forum.project.application.exception.ErrorCode;
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
}
