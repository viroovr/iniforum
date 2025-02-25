package com.forum.project.core.common;

import java.time.LocalDateTime;
import java.util.Map;

public class KeyUtil {
    public static Long getLongValue(Map<String, Object> keys, String key) {
        Object value = keys.get(key);
        return (value instanceof Number) ? ((Number) value).longValue() : null;
    }

    public static LocalDateTime getDateValue(Map<String, Object> keys, String key) {
        return DateUtils.convertToLocalDateTime(keys.get(key));
    }
}
