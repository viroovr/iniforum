package com.forum.project.presentation.exception;

import com.forum.project.domain.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
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
        ErrorCode errorCode = ex.getErrorCode();
        return exceptionResponseUtil.createErrorResponsev2(errorCode.getCode(), errorCode.getMessage(), errorCode.getStatus(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return exceptionResponseUtil.createErrorResponse("INTERNAL_SERVER_ERROR", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException ex) {
        return exceptionResponseUtil.createErrorResponse("REFRESH_TOKEN_MISSING", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTokenException(InvalidTokenException ex) {
        return exceptionResponseUtil.createErrorResponse("INVALID_TOKEN", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleQuestionNotFoundException(QuestionNotFoundException ex) {
        return exceptionResponseUtil.createErrorResponse("QUESTION_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialException(BadCredentialsException ex) {
        return exceptionResponseUtil.createErrorResponse("BAD_CREDENTIAL", ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return exceptionResponseUtil.createErrorResponse("ILLEGAL_ARGUMENT", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
