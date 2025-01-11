package com.forum.project.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.project.application.exception.ErrorCode;

public class LogHelper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String formatLogMessage(
            String path, String method, ErrorCode code, Object details
    ) {
        try {
            String detailsJson = objectMapper.writeValueAsString(details);
            return String.format(
                    "{\"code\":\"%s\", \"message\":\"%s\", \"status\":%d, \"path\":\"%s\", \"method\":\"%s\", \"details\":%s}",
                    code.getCode(), code.getMessage(), code.getStatus().value(), path, method, detailsJson);
        } catch (Exception e) {
            return String.format(
                    "{\"code\":\"%s\", \"message\":\"%s\", \"status\":%d, \"path\":\"%s\", \"method\":\"%s\", \"details\":\"Error serializing details\"}",
                    code.getCode(), code.getMessage(), code.getStatus().value(), path, method);
        }
    }
}
