package com.forum.project.domain.question.like;

import com.forum.project.domain.like.BaseLike;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class QuestionLike extends BaseLike {
    private Long questionId;
}
