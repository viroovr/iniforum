package com.forum.project.core.base;

import com.forum.project.domain.like.vo.LikeStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseLike extends BaseEntity {
    private Long userId;
    @Builder.Default
    private String status = LikeStatus.NONE.name();
    private String ipAddress;

    public void like() {
        this.status = LikeStatus.LIKE.name();
    }

    public void dislike() {
        this.status = LikeStatus.DISLIKE.name();
    }
}
