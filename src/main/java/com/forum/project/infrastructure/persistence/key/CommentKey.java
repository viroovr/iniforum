package com.forum.project.infrastructure.persistence.key;

import com.forum.project.core.base.BaseKey;
import com.forum.project.core.common.KeyUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

import static com.forum.project.core.common.KeyUtil.getDateValue;
import static com.forum.project.core.common.KeyUtil.getLongValue;

@Getter
@SuperBuilder
public class CommentKey extends BaseKey {
    private final LocalDateTime lastModifiedDate;

    public static final String LAST_MODIFIED_DATE = "last_modified_date";

    public static String[] getKeys() {
        return Stream.concat(
                Stream.of(BaseKey.getBaseKeys()),
                Stream.of(new String[] {LAST_MODIFIED_DATE})
        ).toArray(String[]::new);
    }

    public CommentKey(Map<String, Object> keys) {
        super(getLongValue(keys, ID), getDateValue(keys, CREATED_DATE));
        this.lastModifiedDate = getDateValue(keys, LAST_MODIFIED_DATE);
    }
}
