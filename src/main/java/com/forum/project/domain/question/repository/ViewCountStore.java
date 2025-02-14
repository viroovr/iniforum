package com.forum.project.domain.question.repository;

import com.forum.project.domain.question.entity.ViewCount;

import java.util.Set;

public interface ViewCountStore {
    ViewCount getViewCount(String key);
    void setViewCount(String key, Long count);
    void increment(String key);
    void decrement(String key);
    Boolean hasKey(String key);

    Set<String> getAllKeys();

    void clearAllKeys();
}
