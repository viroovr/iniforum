package com.forum.project.infrastructure.persistence.key;

import com.forum.project.core.base.BaseKey;

import java.time.LocalDateTime;

public class CommentLikeKey extends BaseKey {
    public CommentLikeKey(Long id, LocalDateTime createdDate) {
        super(id, createdDate);
    }

    public static String[] getKeys() {
        return BaseKey.getBaseKeys();
    }
}
