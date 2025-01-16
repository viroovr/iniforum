package com.forum.project.infrastructure.jwt;

import java.util.Optional;

public interface TokenCacheRepository {
    <T> void put(String token, T claims);
    <T> Optional<T> get(String token);
}
