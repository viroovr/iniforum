package com.forum.project.presentation.exception;

import com.forum.project.domain.exception.*;
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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        return exceptionResponseUtil.createErrorResponse("USER_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, String>> handleApplicationException(ApplicationException ex, WebRequest request) {
        log.error("Application Error occurred: {}", ex.getMessage());
        ErrorCode errorCode = ex.getErrorCode();
        return exceptionResponseUtil.createErrorResponsev2(errorCode.getCode(), errorCode.getMessage(), errorCode.getStatus(), request);
    }

    @ExceptionHandler(CustomDatabaseException.class)
    public ResponseEntity<Map<String, String >> handleCustomDatabaseException(CustomDatabaseException ex, WebRequest request) {
        log.error("CustomDatabase Error occurred: {}", ex.getMessage());
        ErrorCode errorCode = ex.getErrorCode();
        return exceptionResponseUtil.createErrorResponsev2(errorCode.getCode(), errorCode.getMessage(), errorCode.getStatus(), request);
    }

    @ExceptionHandler(DataAccessException.class)
    public void handleDatabaseException(DataAccessException ex, WebRequest request) {
        log.error("DatabaseAccess Error occurred: {}", ex.getMessage());
        throw new CustomDatabaseException(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return exceptionResponseUtil.createErrorResponse("INTERNAL_SERVER_ERROR", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
