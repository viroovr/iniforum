package com.forum.project.domain.like.entity;

import com.forum.project.core.common.DateUtils;
import com.forum.project.core.base.BaseLike;
import com.forum.project.infrastructure.persistence.key.QuestionLikeKey;
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
        this.setCreatedDate(DateUtils.convertToLocalDateTime(keys.get(QuestionLikeKey.CREATED_DATE)));
    }
}
