package com.forum.project.presentation.dtos.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseCommentDto {
    private Long id;
    private String content;
    private String loginId;
    private LocalDateTime createdDate;
    private Long likeCount;
}
