package com.forum.project.domain.comment.entity;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.core.common.DateUtils;
import com.forum.project.core.base.BaseEntity;
import com.forum.project.infrastructure.persistence.key.CommentKey;
import com.forum.project.domain.comment.vo.CommentStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Comment extends BaseEntity {
    private Long userId;
    private Long questionId;
    private Long parentCommentId;
    private String loginId;
    private String content;
    @Builder.Default
    private Long upVotedCount = 0L;
    @Builder.Default
    private Long downVotedCount = 0L;
    @Builder.Default
    private String status = CommentStatus.ACTIVE.name();
    @Builder.Default
    private Long reportCount = 0L;
    @Builder.Default
    private Boolean isEdited = false;
    private LocalDateTime lastModifiedDate;

    public void setKeys(Map<String, Object> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Keys map cannot be null");
        }

        setId((Long) keys.get(CommentKey.ID));
        setCreatedDate(DateUtils.convertToLocalDateTime(keys.get(CommentKey.CREATED_DATE)));
        this.lastModifiedDate = DateUtils.convertToLocalDateTime(keys.get(CommentKey.LAST_MODIFIED_DATE));
    }

    public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_COMMENT_CONTENT);
        }
        this.content = newContent;
    }

    public void delete() {
        this.status = CommentStatus.DELETED.name();
    }

    public void active() {
        this.status = CommentStatus.ACTIVE.name();
    }

    public void pending() {
        this.status = CommentStatus.PENDING.name();
    }

    public void increaseReportCount() {
        this.reportCount++;
    }

    public void increaseUpVote() {
        this.upVotedCount++;
    }

    public void increaseDownVote() {
        this.downVotedCount++;
    }

    public boolean isDeleted() {
        return Objects.equals(this.status, CommentStatus.DELETED.name());
    }

    public boolean isReply() {
        return this.parentCommentId != null;
    }

    public void validateContent() {
        if (this.content == null || this.content.trim().isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_COMMENT_CONTENT);
        }
    }
}