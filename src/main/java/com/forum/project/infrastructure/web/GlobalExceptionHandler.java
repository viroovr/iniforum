package com.forum.project.infrastructure.web;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.InfraErrorCode;
import com.forum.project.core.common.LogHelper;
import com.forum.project.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        String path = request.getDescription(false).replace("uri=", "");
        String method = ((ServletWebRequest) request).getHttpMethod().name();
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        String errorMessage = ex
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.error(LogHelper.formatLogMessage(path, method, errorCode, errorMessage));

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponseDto(errorCode));
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponseDto> handleApplicationException(ApplicationException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        String method = ((ServletWebRequest) request).getHttpMethod().name();
        ErrorCode errorCode = ex.getErrorCode();

        log.error(LogHelper.formatLogMessage(path, method, errorCode, ex.getDetails()));

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponseDto(errorCode));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponseDto> handleDataAccessException(Exception ex, WebRequest request) {
        InfraErrorCode errorCode = InfraErrorCode.FAIL_ACCESS;
        String path = request.getDescription(false).replace("uri=", "");
        String method = ((ServletWebRequest) request).getHttpMethod().name();

        log.error(LogHelper.formatLogMessage(path, method, errorCode, ex.getMessage()));

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponseDto(errorCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex, WebRequest request) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        String path = request.getDescription(false).replace("uri=", "");
        String method = ((ServletWebRequest) request).getHttpMethod().name();

        log.error(LogHelper.formatLogMessage(path, method, errorCode, ex.getMessage()));

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponseDto(errorCode));
    }
}
