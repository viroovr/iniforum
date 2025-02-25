package com.forum.project.domain.bookmark.entity;

import com.forum.project.domain.bookmark.vo.BookmarkKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {
    private Long id;
    private Long userId;
    private Long questionId;
    private String notes;
    private LocalDateTime lastAccessedDate;
    private LocalDateTime createdDate;

    public void setKeys(BookmarkKey keys) {
        this.id = keys.getId();
        this.createdDate = keys.getCreatedDate();
        this.lastAccessedDate = keys.getLastAccessedDate();
    }
}
