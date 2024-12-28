package com.forum.project.domain.commentlike;

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
