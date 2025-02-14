package com.forum.project.infrastructure.persistence.key;

import com.forum.project.core.base.BaseKey;

import java.util.stream.Stream;

public class BookmarkKey extends BaseKey {
    public static final String LAST_ACCESSED_DATE = "last_accessed_date";

    public static String[] getKeys() {
        return Stream.concat(
                Stream.of(BaseKey.getBaseKeys()),
                Stream.of(new String[] {LAST_ACCESSED_DATE})
        ).toArray(String[]::new);
    }
}
