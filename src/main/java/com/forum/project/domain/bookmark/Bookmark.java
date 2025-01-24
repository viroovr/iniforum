package com.forum.project.domain.bookmark;

import com.forum.project.common.utils.DateUtil;
import com.forum.project.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Bookmark extends BaseEntity {
    private Long userId;
    private Long questionId;
    private String notes;
    private LocalDateTime lastAccessedDate;

    public void setKeys(Map<String, Object> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Keys map cannot be null");
        }

        setId((Long) keys.get(BookmarkKey.ID));
        setCreatedDate(DateUtil.convertToLocalDateTime(keys.get(BookmarkKey.CREATED_DATE)));
        this.lastAccessedDate = DateUtil.convertToLocalDateTime(keys.get(BookmarkKey.LAST_ACCESSED_DATE));
    }
}
