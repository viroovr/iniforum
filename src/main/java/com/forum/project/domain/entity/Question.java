package com.forum.project.domain.entity;

import com.forum.project.application.question.QuestionStatus;
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
    private QuestionStatus status;
    private Long viewCount;
    private Long upVotedCount;
    private Long downVotedCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
