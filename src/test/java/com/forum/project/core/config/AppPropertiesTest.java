package com.forum.project.core.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableConfigurationProperties(AppProperties.class)
class AppPropertiesTest {

    @Autowired
    private AppProperties appProperties;

    @Test
    void testAppPropertiesValues() {
        assertThat(appProperties.getUrl()).isEqualTo("http://localhost:8080");
        assertThat(appProperties.getAdminEmail()).isEqualTo("admin@example.com");
    }
}