package com.forum.project.domain.auth.vo;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequest {
    private Long userId;
    private String loginId;
    private String userRole;

    public static final String USER_ID_CLAIM_KEY = "userId";
    public static final String LOGIN_ID_CLAIM_KEY = "loginId";
    public static final String USER_ROLE_CLAIM_KEY = "userRole";

    public ClaimRequest(Claims claims) {
        this.userId = claims.get(USER_ID_CLAIM_KEY, Long.class);
        this.loginId = claims.get(LOGIN_ID_CLAIM_KEY, String.class);
        this.userRole = claims.get(USER_ROLE_CLAIM_KEY, String.class);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(USER_ID_CLAIM_KEY, userId);
        claimsMap.put(LOGIN_ID_CLAIM_KEY, loginId);
        claimsMap.put(USER_ROLE_CLAIM_KEY, userRole);
        return claimsMap;
    }
}
