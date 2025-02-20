package com.forum.project.domain.auth.entity;

import com.forum.project.core.common.ClockUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetToken {
    private String token;
    private String email;
    private LocalDateTime expiryDate;

    public boolean isExpired() {
        return expiryDate.isBefore(ClockUtil.now());
    }
}
