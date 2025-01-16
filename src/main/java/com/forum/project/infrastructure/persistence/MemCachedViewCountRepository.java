//package com.forum.project.infrastructure.persistence;
//
//import com.forum.project.domain.question.ViewCountRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import net.rubyeye.xmemcached.MemcachedClient;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Repository;
//
//@Repository
//@RequiredArgsConstructor
//@Profile("memcached")
//@Slf4j
//public class MemCachedViewCountRepository implements ViewCountRepository {
//
//    private final MemcachedClient memcachedClient;
//
//    private final String TOTAL_COUNT_KEY = "question:totalCount";
//
//    @Override
//    public void save(Long count) {
//        try {
//            memcachedClient.set(TOTAL_COUNT_KEY, 3600, count); // TTL 설정
//        } catch (Exception e) {
//            log.error("Failed to set total count in Memcached");
//            throw new CustomDatabaseException(e.getMessage());
//        }
//    }
//
//    @Override
//    public Long getTotalCount() {
//        try {
//            return memcachedClient.get(TOTAL_COUNT_KEY);
//        } catch (Exception e) {
//            log.error("Failed to get total count from Memcached");
//            throw new CustomDatabaseException(e.getMessage());
//        }
//    }
//
//    @Override
//    public void increment() {
//        try {
//            Long current = getTotalCount();
//            if (current != null) {
//                save(current + 1);
//            }
//        } catch (Exception e) {
//            log.error("Failed to increment total count in Memcached");
//            throw new CustomDatabaseException(e.getMessage());
//        }
//    }
//
//    @Override
//    public void decrement() {
//        try {
//            Long current = getTotalCount();
//            if (current != null) {
//                save(current - 1);
//            }
//        } catch (Exception e) {
//            log.error("Failed to decrement total count in Memcached");
//            throw new CustomDatabaseException(e.getMessage());
//        }
//    }
//}
