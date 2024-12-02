package com.forum.project.presentation.config;

import com.forum.project.presentation.dtos.EmailVerification;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

@TestConfiguration
public class TestRedisConfig {

    private RedisServer redisServer;

    @Bean
    public RedisServer redisServer() {
        redisServer = new RedisServerBuilder()
                .redisExecProvider(RedisExecProvider.defaultProvider())
                .setting("heapdir D:\\redis")
                .setting("maxheap 64mb")
                .port(6379)
                .build();
        redisServer.start();
        return redisServer;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    public RedisTemplate<String, EmailVerification> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, EmailVerification> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
