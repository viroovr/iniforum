package com.forum.project.domain.question;

import com.forum.project.domain.BaseKey;

import java.util.stream.Stream;

public class QuestionKey extends BaseKey {
    public static final String LAST_MODIFIED_DATE = "last_modified_date";

    public static String[] getKeys() {
        return Stream.concat(
                Stream.of(BaseKey.getBaseKeys()),
                Stream.of(new String[] {LAST_MODIFIED_DATE})
        ).toArray(String[]::new);
    }
}
