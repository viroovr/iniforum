package com.forum.project.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentLike {
    private Long id;
    private Long userId;
    private Long commentId;
    private String ipAddress;
}
