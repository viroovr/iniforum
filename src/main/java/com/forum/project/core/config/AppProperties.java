package com.forum.project.core.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

@Getter
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private final String url;
    private final String adminEmail;

    public AppProperties(String url, String adminEmail) {
        this.url = url;
        this.adminEmail = adminEmail;
    }
}
