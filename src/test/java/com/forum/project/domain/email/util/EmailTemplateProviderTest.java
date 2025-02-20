package com.forum.project.domain.email.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTemplateProviderTest {

    private EmailTemplateProvider emailTemplateProvider;

    private static final String sourcePath = "templates/emails";

    @BeforeEach
    void setUp() {
        emailTemplateProvider = new EmailTemplateProvider();
    }

    @Test
    void getVerificationEmail() throws IOException {
        String expectedTemplate = Files.readString(
                new ClassPathResource(sourcePath + "/verification-email.html").getFile().toPath());

        String result = emailTemplateProvider.getVerificationEmail("123456");

        assertThat(result).isEqualTo(expectedTemplate.replace("%s", "123456"));
    }

    @Test
    void getPasswordResetEmail() throws IOException {
        String resetLink = "https://test.com/reset-password?token=abcdef";

        String expectedTemplate = Files.readString(
                new ClassPathResource(sourcePath + "/password-reset.html").getFile().toPath());

        String result = emailTemplateProvider.getPasswordResetEmail(resetLink);

        assertThat(result).isEqualTo(expectedTemplate.replace("%s", resetLink));
    }
}