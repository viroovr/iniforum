package com.forum.project.domain.report;

import com.forum.project.domain.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseReport extends BaseEntity {

    private Long userId;
    private String reason;
    @Builder.Default
    private String status = ReportStatus.PENDING.name();

    public void markAsResolved() {
        this.status = ReportStatus.RESOLVED.name();
    }

    public abstract void validateReason();
}
