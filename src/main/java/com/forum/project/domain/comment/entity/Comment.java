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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Long id;
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
    private LocalDateTime createdDate;

    public void setKeys(CommentKey key) {
        this.id = key.getId();
        this.createdDate = key.getCreatedDate();
        this.lastModifiedDate = key.getLastModifiedDate();
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

    public void validateOwner(Long userId) {
        if (this.userId.equals(userId)) return;
        throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL, "댓글에 대한 권한이 없습니다.");
    }

    public void validateQuestionId(Long questionId) {
        if (this.questionId.equals(questionId)) return;
        throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL, "해당 질문에 속하는 댓글이 아닙니다.");
    }
}