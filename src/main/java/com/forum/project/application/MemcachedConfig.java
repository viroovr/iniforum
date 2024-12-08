package com.forum.project.application;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("memcached")
public class MemcachedConfig {

    @Bean
    public MemcachedClient memcachedClient() throws Exception {
        return new XMemcachedClientBuilder("localhost:11211").build();
    }
}