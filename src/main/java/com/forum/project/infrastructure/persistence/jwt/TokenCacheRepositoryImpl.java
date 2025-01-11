package com.forum.project.infrastructure.persistence.jwt;

import com.forum.project.domain.jwt.TokenCacheRepository;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenCacheRepositoryImpl implements TokenCacheRepository {

    private final Map<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();
    private final long ttlMillis = 60000; // 60초

    @Override
    public <T> void put(String token, T claims) {
        cache.put(token, new CacheEntry<>(claims, System.currentTimeMillis()));
    }

    @Override
    public <T> Optional<T> get(String token) {
        CacheEntry<?> entry = cache.get(token); // CacheEntry<?>로 가져옴
        if (entry == null || isExpired(entry)) {
            cache.remove(token);
            return Optional.empty();
        }
        return Optional.of((T) entry.data); // 타입 캐스팅
    }

    private boolean isExpired(CacheEntry<?> entry) {
        return System.currentTimeMillis() - entry.timestamp > ttlMillis;
    }

    // CacheEntry는 불변 객체로 설계
    private static class CacheEntry<T> {
        private final T data;
        private final long timestamp;

        public CacheEntry(T data, long timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }
    }
}
