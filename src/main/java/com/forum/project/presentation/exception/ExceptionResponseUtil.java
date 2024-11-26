package com.forum.project.presentation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExceptionResponseUtil {

    public ResponseEntity<Map<String, String>> createErrorResponse(
            String errorCode, String errorMessage, HttpStatus httpStatus
    ) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("error", errorCode);
        errorResponse.put("message", errorMessage);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    public ResponseEntity<Map<String, String>> createErrorResponsev2(
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

    public ResponseEntity<Map<String, String>> createInvalidResponse(String message, WebRequest request) {
        return createErrorResponsev2(
                "VALIDATION_ERROR", message, HttpStatus.BAD_REQUEST, request
        );
    }
}
