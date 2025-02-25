package com.forum.project.domain.email.util;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;

@Component
public class EmailTemplateProvider {
    private static final String SOURCE_PATH = "templates/emails";

    public String getVerificationEmail(String code) {
        return loadTemplate(SOURCE_PATH + "/verification-email.html").replace("%s", code);
    }

    public String getPasswordResetEmail(String resetLink) {
        return loadTemplate(SOURCE_PATH + "/password-reset.html").replace("%s", resetLink);
    }

    public void testFileNotFound() {
        loadTemplate(SOURCE_PATH + "/invalid.html");
    }

    private String loadTemplate(String path) {
        try {
            return Files.readString(new ClassPathResource(path).getFile().toPath());
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.INVALID_REQUEST , "Failed to load email template :" + path);
        }
    }
}
