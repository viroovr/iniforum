package com.forum.project.domain.bookmark.vo;

import com.forum.project.core.base.BaseKey;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

import static com.forum.project.core.common.KeyUtil.getDateValue;
import static com.forum.project.core.common.KeyUtil.getLongValue;

@Getter
public class BookmarkKey extends BaseKey {
    private final LocalDateTime lastAccessedDate;

    public static final String LAST_ACCESSED_DATE = "last_accessed_date";

    public static String[] getKeys() {
        return Stream.concat(
                Stream.of(BaseKey.getBaseKeys()),
                Stream.of(new String[] {LAST_ACCESSED_DATE})
        ).toArray(String[]::new);
    }

    public BookmarkKey(Map<String, Object> keys) {
        super(getLongValue(keys, ID), getDateValue(keys, CREATED_DATE));
        this.lastAccessedDate = getDateValue(keys, LAST_ACCESSED_DATE);
    }
}
