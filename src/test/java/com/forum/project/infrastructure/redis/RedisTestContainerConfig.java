package com.forum.project.infrastructure.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class RedisTestContainerConfig implements BeforeAllCallback {
    private static final String REDIS_IMAGE = "redis:7.0.8-alpine";
    private static final int REDIS_PORT = 6379;
    private GenericContainer redisGenericContainer;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        redisGenericContainer = new GenericContainer(DockerImageName.parse(REDIS_IMAGE))
                .withExposedPorts(REDIS_PORT);

        redisGenericContainer.start();

        String host = redisGenericContainer.getHost();
        String port = String.valueOf(redisGenericContainer.getMappedPort(REDIS_PORT));
        System.setProperty("spring.data.redis.host", host);
        System.setProperty("spring.data.redis.port", port);
        log.info("Started Redis container at {} : {}",host, port);
    }
}
