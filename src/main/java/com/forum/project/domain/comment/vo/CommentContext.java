package com.forum.project.domain.comment.vo;

import lombok.Builder;

@Builder
public record CommentContext(Long userId, Long questionId, Long commentId) {
}
