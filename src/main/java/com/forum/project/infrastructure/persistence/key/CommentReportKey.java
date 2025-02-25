package com.forum.project.infrastructure.persistence.key;

import com.forum.project.core.base.BaseKey;

import java.time.LocalDateTime;

public class CommentReportKey extends BaseKey {
    public CommentReportKey(Long id, LocalDateTime createdDate) {
        super(id, createdDate);
    }

    public static String[] getKeys() {
        return BaseKey.getBaseKeys();
    }
}
