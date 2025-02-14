package com.forum.project.domain.question.entity;

import com.forum.project.core.common.DateUtils;
import com.forum.project.core.base.BaseEntity;
import com.forum.project.infrastructure.persistence.key.CommentKey;
import com.forum.project.domain.question.vo.QuestionStatus;
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
public class Question extends BaseEntity {
    private Long userId;
    private String title;
    private String content;
    @Builder.Default
    private String status = QuestionStatus.OPEN.name();
    @Builder.Default
    private Long viewCount = 0L;
    @Builder.Default
    private Long upVotedCount = 0L;
    @Builder.Default
    private Long downVotedCount = 0L;
    private LocalDateTime lastModifiedDate;

    public void setKeys(Map<String, Object> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Keys map cannot be null");
        }

        setId((Long) keys.get(CommentKey.ID));
        setCreatedDate(DateUtils.convertToLocalDateTime(keys.get(CommentKey.CREATED_DATE)));
        this.lastModifiedDate = DateUtils.convertToLocalDateTime(keys.get(CommentKey.LAST_MODIFIED_DATE));
    }

    public void open() {
        if ("OPEN".equals(this.status)) {
            throw new IllegalStateException("Question is already open.");
        }
        this.status = "OPEN";
    }

    public void close() {
        if ("CLOSED".equals(this.status)) {
            throw new IllegalStateException("Question is already closed.");
        }
        this.status = "CLOSED";
    }

    public void markAsResolved() {
        if ("RESOLVED".equals(this.status)) {
            throw new IllegalStateException("Question is already resolved.");
        }
        this.status = "RESOLVED";
    }

    public void validate() {
        if (this.title == null || this.title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title must not be empty.");
        }
        if (this.content == null || this.content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content must not be empty.");
        }
        if (this.status == null || this.status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status must not be empty.");
        }

        if (this.title.length() > 100) {
            throw new IllegalArgumentException("Title cannot be longer than 100 characters.");
        }
        if (this.content.length() > 1000) {
            throw new IllegalArgumentException("Content cannot be longer than 1000 characters.");
        }
    }

    // 추가적으로 조회수를 증가시키는 메서드
    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementUpVotedCount() {
        this.upVotedCount++;
    }

    public void decrementUpVotedCount() {
        if (this.upVotedCount > 0)
            this.upVotedCount--;
    }

    public void incrementDownVotedCount() {
        this.downVotedCount++;
    }

    public void decrementDownVotedCount() {
        if (this.downVotedCount > 0)
            this.downVotedCount--;
    }
}
