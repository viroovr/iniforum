package com.forum.project.presentation.dtos.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCommentDto {
    private Long id;

    private String content;

    private String userId;

    private LocalDateTime createdDate;

    private Long likeCount;
}
