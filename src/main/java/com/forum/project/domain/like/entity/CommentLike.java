package com.forum.project.domain.like.entity;

import com.forum.project.core.common.DateUtils;
import com.forum.project.core.base.BaseLike;
import com.forum.project.infrastructure.persistence.key.CommentReportKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommentLike extends BaseLike {
    private Long commentId;

    @Override
    public void setKeys(Map<String, Object> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Keys map cannot be null");
        }

        setId((Long) keys.get(CommentReportKey.ID));
        setCreatedDate(DateUtils.convertToLocalDateTime(keys.get(CommentReportKey.CREATED_DATE)));
    }
}
