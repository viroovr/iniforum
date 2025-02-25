package com.forum.project.domain.report.mapper;

import com.forum.project.domain.comment.vo.CommentContext;
import com.forum.project.domain.report.dto.CommentReportCreateDto;
import com.forum.project.domain.report.dto.ReportRequestDto;
import com.forum.project.domain.report.vo.ReportStatus;

public class CommentReportDtoMapper {
    public static CommentReportCreateDto toCommentReportCreateDto(ReportRequestDto dto, CommentContext context) {
        return CommentReportCreateDto.builder()
                .userId(context.userId())
                .commentId(context.commentId())
                .reason(dto.getReason())
                .status(ReportStatus.PENDING)
                .details(dto.getDetails())
                .build();
    }
}
