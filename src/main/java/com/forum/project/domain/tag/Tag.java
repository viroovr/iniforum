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
    private Long usageCount;
    private Boolean isActive;
    private String category;
}
