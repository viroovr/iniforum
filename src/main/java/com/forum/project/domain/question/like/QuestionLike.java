package com.forum.project.domain.question.like;

import com.forum.project.common.utils.DateUtil;
import com.forum.project.domain.like.BaseLike;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class QuestionLike extends BaseLike {
    private Long questionId;

    public void setKeys(Map<String, Object> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Keys map cannot be null");
        }

        this.setId((Long) keys.get(QuestionLikeKey.ID));
        this.setCreatedDate(DateUtil.convertToLocalDateTime(keys.get(QuestionLikeKey.CREATED_DATE)));
    }
}
