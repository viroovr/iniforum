package com.forum.project.presentation.comment;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    @Size(min = 1, max = 1000, message = "{comment.length}")
    private String content;
}
