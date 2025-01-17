package com.forum.project.domain.like.commentlike;

import com.forum.project.domain.like.BaseLike;
import com.forum.project.domain.like.LikeStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CommentLike extends BaseLike {
    private Long commentId;
}
