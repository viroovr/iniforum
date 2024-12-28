package com.forum.project.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class RepositoryConfig {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
