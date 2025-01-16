package com.forum.project.domain.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseReport {
    private Long id;
    private Long userId;
    private String reason;
    private LocalDateTime reportDate;
    private boolean isResolved;

    public void initialize(Long userId, String reason) {
        this.userId = userId;
        this.reason = reason;
        this.reportDate = LocalDateTime.now();
        this.isResolved = false;
    }

    public void markAsResolved() {
        this.isResolved = true;
    }

    public abstract void validateReason();
}
