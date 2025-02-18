package com.forum.project.core.base;

import com.forum.project.core.common.DateUtils;

import java.time.LocalDateTime;
import java.util.Map;

public abstract class BaseKey {
    public static final String ID = "id";
    public static final String CREATED_DATE = "created_date";

    public static String[] getBaseKeys() {
        return new String[] {ID, CREATED_DATE};
    }

    public Long getLongValue(Map<String, Object> keys, String key) {
        Object value = keys.get(key);
        return (value instanceof Number) ? ((Number) value).longValue() : null;
    }

    public LocalDateTime getDateValue(Map<String, Object> keys, String key) {
        return DateUtils.convertToLocalDateTime(keys.get(key));
    }
}
