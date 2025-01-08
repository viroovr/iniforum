package com.forum.project.infrastructure.persistence;

import com.forum.project.domain.question.TotalCountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Profile("memcached")
@Slf4j
public class MemCachedTotalCountRepository implements TotalCountRepository {

    private final MemcachedClient memcachedClient;

    private final String TOTAL_COUNT_KEY = "question:totalCount";

    @Override
    public void setTotalCount(Long count) {
        try {
            memcachedClient.set(TOTAL_COUNT_KEY, 3600, count); // TTL 설정
        } catch (Exception e) {
            log.error("Failed to set total count in Memcached");
            throw new CustomDatabaseException(e.getMessage());
        }
    }

    @Override
    public Long getTotalCount() {
        try {
            return memcachedClient.get(TOTAL_COUNT_KEY);
        } catch (Exception e) {
            log.error("Failed to get total count from Memcached");
            throw new CustomDatabaseException(e.getMessage());
        }
    }

    @Override
    public void incrementTotalCount() {
        try {
            Long current = getTotalCount();
            if (current != null) {
                setTotalCount(current + 1);
            }
        } catch (Exception e) {
            log.error("Failed to increment total count in Memcached");
            throw new CustomDatabaseException(e.getMessage());
        }
    }

    @Override
    public void decrementTotalCount() {
        try {
            Long current = getTotalCount();
            if (current != null) {
                setTotalCount(current - 1);
            }
        } catch (Exception e) {
            log.error("Failed to decrement total count in Memcached");
            throw new CustomDatabaseException(e.getMessage());
        }
    }
}
