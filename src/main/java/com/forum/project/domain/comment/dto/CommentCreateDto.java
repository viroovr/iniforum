package com.forum.project.domain.comment.dto;

import com.forum.project.domain.comment.vo.CommentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDto {
    private Long userId;
    private Long questionId;
    private Long parentCommentId;
    private String content;
    private CommentStatus status;

    public static CommentCreateDtoBuilder fromCommentRequestDto(CommentRequestDto commentRequestDto) {
        return CommentCreateDto.builder()
                .content(commentRequestDto.getContent())
                .parentCommentId(commentRequestDto.getParentCommentId());
    }
}
