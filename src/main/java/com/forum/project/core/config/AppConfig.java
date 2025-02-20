package com.forum.project.core.config;

import com.forum.project.domain.auth.vo.TokenExpirationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AppProperties.class, TokenExpirationProperties.class})
public class AppConfig {
}
