package com.forum.project.core.base;

public abstract class BaseKey {
    public static final String ID = "id";
    public static final String CREATED_DATE = "created_date";

    public static String[] getBaseKeys() {
        return new String[] {ID, CREATED_DATE};
    }
}
