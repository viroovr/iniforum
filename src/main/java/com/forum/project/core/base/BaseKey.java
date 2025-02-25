package com.forum.project.core.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
public class BaseKey {
    private final Long id;
    private final LocalDateTime createdDate;

    public static final String ID = "id";
    public static final String CREATED_DATE = "created_date";

    public static String[] getBaseKeys() {
        return new String[] {ID, CREATED_DATE};
    }
}
