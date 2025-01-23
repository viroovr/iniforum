package com.forum.project.domain.comment;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public void initialize(Long questionId, Long userId, String loginId) {
        this.questionId = questionId;
        this.userId = userId;
        this.loginId= loginId;
        this.upVotedCount = 0L;
        this.downVotedCount = 0L;
        this.reportCount = 0L;
        this.isEdited = false;
        this.status = CommentStatus.ACTIVE.name();
    }

    public void markAsEdited(LocalDateTime now) {
        this.isEdited = true;
        this.lastModifiedDate = now;
    }

    public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_COMMENT_CONTENT);
        }
        LocalDateTime now = LocalDateTime.now();
        this.content = newContent;
        this.lastModifiedDate = now;
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