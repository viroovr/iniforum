package com.forum.project.domain.tag;

import com.forum.project.common.utils.DateUtils;
import com.forum.project.domain.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Tag extends BaseEntity {
    private String name;
    @Builder.Default
    private Long usageCount = 0L;
    private LocalDateTime lastModifiedDate;

    @Override
    public void setKeys(Map<String, Object> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Keys map cannot be null");
        }

        setId((Long) keys.get(TagKey.ID));
        setCreatedDate(DateUtils.convertToLocalDateTime(keys.get(TagKey.CREATED_DATE)));
        setLastModifiedDate(DateUtils.convertToLocalDateTime(keys.get(TagKey.LAST_MODIFIED_DATE)));
    }

    public void incrementUsageCount() {
        this.usageCount++;
    }
}
