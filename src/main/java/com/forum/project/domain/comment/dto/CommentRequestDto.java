package com.forum.project.domain.comment.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    @Size(min = 1, max = 1000, message = "{comment.length}")
    private String content;

    private Long parentCommentId;
}
