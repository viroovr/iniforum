package com.forum.project.domain.user.vo;

import com.forum.project.core.base.BaseKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserKey extends BaseKey {
    private Long id;
    private LocalDateTime createdDate;

    private LocalDateTime lastActivityDate;
    private LocalDateTime lastPasswordModifiedDate;
    private LocalDateTime lastLoginDate;

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
        this.id = getLongValue(keys, ID);
        this.createdDate = getDateValue(keys, CREATED_DATE);
        this.lastActivityDate = getDateValue(keys, LAST_ACTIVITY_DATE);
        this.lastPasswordModifiedDate = getDateValue(keys, LAST_PASSWORD_MODIFIED_DATE);
        this.lastLoginDate = getDateValue(keys, LAST_LOGIN_DATE);
    }
}
