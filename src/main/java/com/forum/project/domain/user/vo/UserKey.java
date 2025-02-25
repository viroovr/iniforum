package com.forum.project.domain.user.vo;

import com.forum.project.core.base.BaseKey;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

import static com.forum.project.core.common.KeyUtil.getDateValue;
import static com.forum.project.core.common.KeyUtil.getLongValue;

@Getter
public class UserKey extends BaseKey {
    private final LocalDateTime lastActivityDate;
    private final LocalDateTime lastPasswordModifiedDate;
    private final LocalDateTime lastLoginDate;

    public static final String LAST_ACTIVITY_DATE = "last_activity_date";
    public static final String LAST_PASSWORD_MODIFIED_DATE = "last_password_modified_date";
    public static final String LAST_LOGIN_DATE = "last_login_date";

    public static String[] getKeys() {
        return Stream.concat(
                Stream.of(BaseKey.getBaseKeys()),
                Stream.of(new String[] {LAST_ACTIVITY_DATE, LAST_PASSWORD_MODIFIED_DATE, LAST_LOGIN_DATE})
        ).toArray(String[]::new);
    }

    public UserKey(Map<String, Object> keys) {
        super(getLongValue(keys, ID), getDateValue(keys, CREATED_DATE));
        this.lastActivityDate = getDateValue(keys, LAST_ACTIVITY_DATE);
        this.lastPasswordModifiedDate = getDateValue(keys, LAST_PASSWORD_MODIFIED_DATE);
        this.lastLoginDate = getDateValue(keys, LAST_LOGIN_DATE);
    }
}
