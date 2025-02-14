package com.forum.project.infrastructure.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@TestConfiguration
@Slf4j
public class TestRedisConfiguration {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        String redisHost = System.getProperty("spring.data.redis.host", "localhost");
        int redisPort = Integer.parseInt(System.getProperty("spring.data.redis.port", "6379"));

        log.info("bean configuration : LettuceConnectionFactory({}, {})", redisHost, redisPort);
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    private <T> RedisTemplate<String, T> createRedisTemplate(LettuceConnectionFactory connectionFactory, Class<T> clazz) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        log.info("bean configuration : {} start", clazz.getSimpleName());
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplateObject(LettuceConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, Object.class);
    }

//    @Bean(name = "redisTemplateLong")
//    public RedisTemplate<String, Long> redisTemplateLong(LettuceConnectionFactory connectionFactory) {
//        return createRedisTemplate(connectionFactory, Long.class);
//    }
}
