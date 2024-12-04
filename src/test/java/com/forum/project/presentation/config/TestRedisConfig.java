package com.forum.project.presentation.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.embedded.RedisServer;

@TestConfiguration
public class TestRedisConfig {

    private static final Logger log = LoggerFactory.getLogger(TestRedisConfig.class);

    @Getter
    private RedisServer redisServer;

    private String host = "localhost";

    private int port = 6379;

    private RedisConnectionFactory redisConnectionFactory;

    @PostConstruct
    public RedisServer redisServer() {
        redisServer = RedisServer.builder()
                .setting("bind 127.0.0.1")
                .setting("heapdir D:\\redis")
                .setting("maxmemory 64M")
                .port(port)
                .build();
        redisServer.start();
        log.info("[debug] Test Redis Server Start");
        return redisServer;
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null && redisServer.isActive()) {
            log.info("[debug] Stopping Test Redis Server...");
            redisServer.stop();
            log.info("[debug] Test Redis Server stopped");
        }
    }

    @PreDestroy
    public void cleanUp() {
        log.info("Closing resources...");

        if (redisConnectionFactory instanceof DisposableBean) {
            try {
                ((DisposableBean) redisConnectionFactory).destroy();
            } catch (Exception e) {
                log.error("Error while closing RedisConnectionFactory", e);
            }
        }

        stopRedis(); // Redis 서버를 멈추는 메서드
        log.info("Resources cleaned up.");
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        this.redisConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        return this.redisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
