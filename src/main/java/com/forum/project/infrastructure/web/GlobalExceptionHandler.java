package com.forum.project.infrastructure.web;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.infrastructure.persistence.CustomDatabaseException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.common.utils.ExceptionResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ExceptionResponseUtil exceptionResponseUtil;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        String errorMessage = ex
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return exceptionResponseUtil.createInvalidResponse(errorMessage, request);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, String>> handleApplicationException(ApplicationException ex, WebRequest request) {
        log.error("Application Error occurred: {}", ex.getMessage());
        ErrorCode errorCode = ex.getErrorCode();
        return exceptionResponseUtil.createErrorResponse(errorCode.getCode(), errorCode.getMessage(), errorCode.getStatus(), request);
    }

    @ExceptionHandler(CustomDatabaseException.class)
    public ResponseEntity<Map<String, String >> handleCustomDatabaseException(CustomDatabaseException ex, WebRequest request) {
        log.error("CustomDatabase Error occurred: {}", ex.getMessage());
        ErrorCode errorCode = ex.getErrorCode();
        return exceptionResponseUtil.createErrorResponse(errorCode.getCode(), errorCode.getMessage(), errorCode.getStatus(), request);
    }

    @ExceptionHandler(DataAccessException.class)
    public void handleDatabaseException(DataAccessException ex, WebRequest request) {
        log.error("DatabaseAccess Error occurred: {}", ex.getMessage());
        throw new CustomDatabaseException(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex, WebRequest request) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return exceptionResponseUtil.createErrorResponse(errorCode.getCode(), ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
