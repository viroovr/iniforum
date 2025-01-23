package com.forum.project.domain.like;

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
public class BaseLike {
    private Long id;
    private Long userId;
    @Builder.Default
    private String status = LikeStatus.NONE.name();
    private String ipAddress;
    private LocalDateTime createdDate;

    public void like() {
        this.status = LikeStatus.LIKE.name();
    }

    public void dislike() {
        this.status = LikeStatus.DISLIKE.name();
    }
}
