package com.forum.project.core.common;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DateUtils {

    /** Timestamp Object를 LocalDateTime으로 변환하는 유틸 메서드
     *
     * @param value - 변환 하려는 Object 객체
     * @return LocalDateTime
     */
    public static LocalDateTime convertToLocalDateTime(Object value) {
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        }
        throw new IllegalArgumentException("Unsupported date type: " + value.getClass().getName());
    }

    /** expected와 actual 사이의 값이 1000ms 이하 확인 메서드
     */
    public static boolean timeDifferenceWithinLimit(Timestamp expected, Timestamp actual) {
        long difference = Math.abs(expected.getTime() - actual.getTime());
        return difference <= 1000;
    }
}
