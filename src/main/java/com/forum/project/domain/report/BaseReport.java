package com.forum.project.domain.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
    @Builder.Default
    private boolean isResolved = false;

    public void markAsResolved() {
        this.isResolved = true;
    }

    public abstract void validateReason();
}
