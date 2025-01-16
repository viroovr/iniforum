package com.forum.project.domain.question;

import java.util.Set;

public interface ViewCountRepository {
    Long getViewCount(String key);
    void setViewCount(String key, Long count);
    void increment(String key);
    void decrement(String key);
    Boolean hasKey(String key);

    Set<String> getAllKeys();

    void clearAllKeys();
}
