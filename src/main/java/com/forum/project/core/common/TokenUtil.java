package com.forum.project.core.common;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;

public class TokenUtil {
    public static String extractToken(String header) {
        if(header != null && header.startsWith("Bearer ")) return header.substring(7);

        throw new ApplicationException(ErrorCode.INVALID_AUTH_HEADER,
                "헤더 값이 null 이거나 헤더가 'Bearer '로 시작하지 않습니다.");
    }
}
