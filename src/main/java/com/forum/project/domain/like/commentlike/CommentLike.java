package com.forum.project.domain.like.commentlike;

import com.forum.project.common.utils.DateUtil;
import com.forum.project.domain.like.BaseLike;
import com.forum.project.domain.report.ReportKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CommentLike extends BaseLike {
    private Long commentId;

    @Override
    public void setKeys(Map<String, Object> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Keys map cannot be null");
        }

        setId((Long) keys.get(ReportKey.ID));
        setCreatedDate(DateUtil.convertToLocalDateTime(keys.get(ReportKey.CREATED_DATE)));
    }
}
