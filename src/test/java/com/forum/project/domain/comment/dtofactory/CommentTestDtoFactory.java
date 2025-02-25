package com.forum.project.domain.comment.dtofactory;

import com.forum.project.domain.comment.dto.CommentCreateDto;
import com.forum.project.domain.comment.dto.CommentRequestDto;
import com.forum.project.domain.comment.dto.CommentResponseDto;
import com.forum.project.domain.comment.entity.Comment;
import com.forum.project.domain.comment.vo.CommentContext;
import com.forum.project.domain.comment.vo.CommentStatus;
import com.forum.project.infrastructure.persistence.key.CommentKey;
import com.forum.project.testUtils.BaseTestDtoFactory;

public class CommentTestDtoFactory extends BaseTestDtoFactory {
    public static CommentKey createCommentKey() {
        return CommentKey.builder()
                .id(1L)
                .lastModifiedDate(DATE_TIME)
                .createdDate(DATE_TIME)
                .build();
    }

    public static CommentCreateDto createCommentCreateDto() {
        return CommentCreateDto.builder()
                .userId(1L)
                .questionId(1L)
                .parentCommentId(null)
                .status(CommentStatus.ACTIVE)
                .content("testContent")
                .build();
    }
    public static CommentContext createCommentContext() {
        return CommentContext.builder()
                .commentId(1L)
                .questionId(1L)
                .userId(1L)
                .build();
    }

    public static Comment createComment() {
        return Comment.builder()
                .id(1L)
                .userId(1L)
                .questionId(1L)
                .parentCommentId(null)
                .loginId("testLoginId")
                .content("testContent")
                .lastModifiedDate(DATE_TIME)
                .createdDate(DATE_TIME)
                .build();
    }

    public static CommentRequestDto createCommentRequestDto() {
        return CommentRequestDto.builder()
                .parentCommentId(1L)
                .content("testContent")
                .build();
    }

    public static CommentResponseDto createCommentResponseDto() {
        return CommentResponseDto.builder()
                .id(1L)
                .content("testContent")
                .loginId("testLoginId")
                .createdDate(DATE_TIME)
                .build();
    }
}
