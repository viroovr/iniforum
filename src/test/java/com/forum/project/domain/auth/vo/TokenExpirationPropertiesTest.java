package com.forum.project.domain.auth.vo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableConfigurationProperties(TokenExpirationProperties.class)
class TokenExpirationPropertiesTest {
    @Autowired
    private TokenExpirationProperties properties;

    @Test
    void testPropertiesLoadedCorrectly() {
        assertThat(properties.getAccessTokenExpTime()).isEqualTo(600);
        assertThat(properties.getRefreshTokenExpTime()).isEqualTo(604800);
    }
}