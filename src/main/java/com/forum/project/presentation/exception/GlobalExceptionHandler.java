package com.forum.project.presentation.exception;

import com.forum.project.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, String>> createErrorResponse(
            String errorCode, String errorMessage, HttpStatus httpStatus
    ) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("error", errorCode);
        errorResponse.put("message", errorMessage);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
    private ResponseEntity<Map<String, String>> createErrorResponsev2(
            String errorCode, String errorMessage, HttpStatus httpStatus, WebRequest request
    ) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", String.valueOf(httpStatus.value()));
        errorResponse.put("error", errorCode);
        errorResponse.put("message", errorMessage);
        errorResponse.put("path",  request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        return createErrorResponse("USER_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Map<String, String>> handleInvalidPasswordException(InvalidPasswordException ex) {
        return createErrorResponse("INVALID_PASSWORD", ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, String>> handleBaseException(ApplicationException ex, WebRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        return createErrorResponsev2(errorCode.getCode(), errorCode.getMessage(), errorCode.getStatus(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return createErrorResponse("INTERNAL_SERVER_ERROR", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException ex) {
        return createErrorResponse("REFRESH_TOKEN_MISSING", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTokenException(InvalidTokenException ex) {
        return createErrorResponse("INVALID_TOKEN", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleQuestionNotFoundException(QuestionNotFoundException ex) {
        return createErrorResponse("QUESTION_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialException(BadCredentialsException ex) {
        return createErrorResponse("BAD_CREDENTIAL", ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return createErrorResponse("ILLEGAL_ARGUMENT", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
