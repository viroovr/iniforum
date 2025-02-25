package com.forum.project.infrastructure.persistence.key;

import com.forum.project.core.base.BaseKey;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class QuestionKey extends BaseKey {
    public static final String LAST_MODIFIED_DATE = "last_modified_date";

    public QuestionKey(Long id, LocalDateTime createdDate) {
        super(id, createdDate);
    }

    public static String[] getKeys() {
        return Stream.concat(
                Stream.of(BaseKey.getBaseKeys()),
                Stream.of(new String[] {LAST_MODIFIED_DATE})
        ).toArray(String[]::new);
    }
}
