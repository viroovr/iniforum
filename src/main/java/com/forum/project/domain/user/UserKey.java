package com.forum.project.domain.user;

import com.forum.project.domain.BaseKey;

import java.util.stream.Stream;

public class UserKey extends BaseKey {
    public static final String LAST_ACTIVITY_DATE = "last_activity_date";
    public static final String LAST_PASSWORD_MODIFIED_DATE = "last_password_modified_date";
    public static final String LAST_LOGIN_DATE = "last_login_date";

    public static String[] getKeys() {
        return Stream.concat(
                Stream.of(BaseKey.getBaseKeys()),
                Stream.of(new String[] {LAST_ACTIVITY_DATE, LAST_PASSWORD_MODIFIED_DATE, LAST_LOGIN_DATE})
                ).toArray(String[]::new);
    }
}
