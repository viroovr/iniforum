package com.forum.project.domain.auth.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@ConfigurationProperties(prefix = "jwt.exp-time")
public class TokenExpirationProperties {
    private final long accessTokenExpTime;
    private final long refreshTokenExpTime;

    public TokenExpirationProperties(
            long accessTokenExpTime,
            long refreshTokenExpTime
    ) {
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
    }
}
