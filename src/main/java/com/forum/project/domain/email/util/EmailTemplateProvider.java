package com.forum.project.domain.email.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;

@Component
public class EmailTemplateProvider {
    private static final String sourcePath = "templates/emails";

    public String getVerificationEmail(String code) {
        return loadTemplate(sourcePath + "/verification-email.html").replace("%s", code);
    }

    public String getPasswordResetEmail(String resetLink) {
        return loadTemplate(sourcePath + "/password-reset.html").replace("%s", resetLink);
    }

    private String loadTemplate(String path) {
        try {
            return Files.readString(new ClassPathResource(path).getFile().toPath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load email template" + path, e);
        }
    }
}
