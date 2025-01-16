package com.forum.project.domain.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    private Long id;
    private Long userId;
    private String loginId;
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
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

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
        if (this.viewCount == null) {
            this.viewCount = 0L;
        }
        this.viewCount++;
    }

    // 유저가 추천한 경우 증가시키는 메서드
    public void incrementUpVotedCount() {
        if (this.upVotedCount == null) {
            this.upVotedCount = 0L;
        }
        this.upVotedCount++;
    }

    // 유저가 반대 의견을 표시한 경우 감소시키는 메서드
    public void incrementDownVotedCount() {
        if (this.downVotedCount == null) {
            this.downVotedCount = 0L;
        }
        this.downVotedCount++;
    }
}
