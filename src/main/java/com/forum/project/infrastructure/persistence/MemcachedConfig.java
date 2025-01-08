package com.forum.project.infrastructure.persistence;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("memcached")
public class MemcachedConfig {

    @Bean
    @Profile("dev")
    public MemcachedClient memcachedClient() throws Exception {
        return new XMemcachedClientBuilder("localhost:11211").build();
    }
}