package com.forum.project.domain.auth.dto;

import jakarta.servlet.http.Cookie;

public class CookieBuilder {
    private final Cookie cookie;

    public CookieBuilder(String name, String value) {
        this.cookie = new Cookie(name, value);
    }

    public CookieBuilder httpOnly(boolean httpOnly) {
        this.cookie.setHttpOnly(httpOnly);
        return this;
    }

    public CookieBuilder path(String path) {
        this.cookie.setPath(path);
        return this;
    }

    public CookieBuilder maxAge(int maxAge) {
        this.cookie.setMaxAge(maxAge);
        return this;
    }

    public Cookie build() {
        return this.cookie;
    }
}
