package com.forum.project.core.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.core.exception.InfraErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogHelper {
    private static final Logger logger = LoggerFactory.getLogger(LogHelper.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

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

    public static String formatLogMessage(
            String path, String method, InfraErrorCode code, Object details
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

    public static void logApplicationException(ApplicationException exception) {
        try {
            String detailsJson = objectMapper.writeValueAsString(exception);
            logger.info("ApplicationException occurred: \n{}", detailsJson);
        } catch (Exception e) {
            logger.error("{\"details\":\"Error serializing details\"}");
        }
    }
}
