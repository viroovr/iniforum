package com.forum.project.domain.email.util;

import com.forum.project.core.exception.ErrorCode;
import com.forum.project.testUtils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTemplateProviderTest {

    private EmailTemplateProvider emailTemplateProvider;

    private static final String SOURCE_PATH = "templates/emails";

    @BeforeEach
    void setUp() {
        emailTemplateProvider = new EmailTemplateProvider();
    }

    private String readEmailTemplate(String templateName) throws IOException {
        return Files.readString(
                new ClassPathResource(SOURCE_PATH + "/" + templateName).getFile().toPath()
        );
    }

    @Test
    void getVerificationEmail() throws IOException {
        String expectedTemplate = readEmailTemplate("/verification-email.html");

        String result = emailTemplateProvider.getVerificationEmail("123456");

        assertThat(result).isEqualTo(expectedTemplate.replace("%s", "123456"));
    }

    @Test
    void getPasswordResetEmail() throws IOException {
        String resetLink = "https://test.com/reset-password?token=abcdef";
        String expectedTemplate = readEmailTemplate("/password-reset.html");

        String result = emailTemplateProvider.getPasswordResetEmail(resetLink);

        assertThat(result).isEqualTo(expectedTemplate.replace("%s", resetLink));
    }

    @Test
    void loadTemplate_FileNotFound() {
        TestUtils.assertApplicationException(
                () -> emailTemplateProvider.testFileNotFound(),
                ErrorCode.INVALID_REQUEST
        );
    }
}