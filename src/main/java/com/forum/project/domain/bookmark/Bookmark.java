package com.forum.project.domain.bookmark;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bookmark {
    private Long id;
    private Long userId;
    private Long questionId;
    private LocalDateTime createdAt;
}
