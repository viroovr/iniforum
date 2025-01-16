package com.forum.project.domain.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private Long usageCount = 0L;
    private Boolean isActive = Boolean.TRUE;
    private String category = "UnCategorized";

    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }
}
