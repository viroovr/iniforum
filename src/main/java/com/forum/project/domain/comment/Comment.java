package com.forum.project.domain.comment;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private Long id;
    private Long userId;
    private String loginId;
    private String content;
    private Long questionId;
    private Long upVotedCount;
    private Long downVotedCount;
    private String status;
    private Long parentCommentId;
    private Long reportCount;
    private Boolean isEdited;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public void initialize(Long questionId, Long userId, String loginId) {
        this.questionId = questionId;
        this.userId = userId;
        this.loginId = loginId;
        this.upVotedCount = 0L;
        this.downVotedCount = 0L;
        this.reportCount = 0L;
        this.isEdited = false;
        this.status = CommentStatus.ACTIVE.name();
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
    }

    public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_COMMENT_CONTENT);
        }
        this.content = newContent;
        this.isEdited = true;
        this.lastModifiedDate = LocalDateTime.now();
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