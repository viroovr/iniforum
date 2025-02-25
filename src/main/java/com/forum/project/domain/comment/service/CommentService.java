package com.forum.project.domain.comment.service;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.comment.dto.CommentCreateDto;
import com.forum.project.domain.comment.dto.CommentRequestDto;
import com.forum.project.domain.comment.dto.CommentResponseDto;
import com.forum.project.domain.comment.entity.Comment;
import com.forum.project.domain.comment.mapper.CommentDtoMapper;
import com.forum.project.domain.comment.repository.CommentRepository;
import com.forum.project.domain.comment.vo.CommentContext;
import com.forum.project.domain.like.service.CommentLikeService;
import com.forum.project.domain.report.dto.ReportRequestDto;
import com.forum.project.domain.report.service.CommentReportService;
import com.forum.project.domain.user.service.UserService;
import com.forum.project.infrastructure.persistence.key.CommentKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final CommentLikeService commentLikeService;
    private final CommentReportService commentReportService;

    private CommentKey insertAndReturnGeneratedKeys(CommentCreateDto dto) {
        return commentRepository.insertAndReturnGeneratedKeys(dto)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DATABASE_ERROR, "댓글 Key 생성 오류"));
    }

    private void validateId(Long id) {
        if (!commentRepository.existsById(id))
            throw new ApplicationException(ErrorCode.COMMENT_NOT_FOUND, "존재하지 않는 Comment");
    }

    @Transactional
    public CommentResponseDto addComment(CommentCreateDto dto) {
        if (dto.getParentCommentId() != null) validateId(dto.getParentCommentId());

        Comment comment = CommentDtoMapper.toEntity(dto, insertAndReturnGeneratedKeys(dto));
        return CommentDtoMapper.toResponseCommentDto(comment, userService.getLoginId(dto.getUserId()));
    }

    private List<CommentResponseDto> convertCommentsList(List<Comment> comments) {
        return comments.stream()
                .map(comment -> {
                    String loginId = userService.getLoginId(comment.getUserId());
                    return CommentDtoMapper.toResponseCommentDto(comment, loginId);
                }).toList();
    }

    public List<CommentResponseDto> getCommentsByQuestionId(Long questionId) {
        return convertCommentsList(commentRepository.findAllByQuestionId(questionId));
    }

    public List<CommentResponseDto> getUserComments(Long userId) {
        return convertCommentsList(commentRepository.findAllByUserId(userId));
    }

    public List<CommentResponseDto> getChildComments(Long parentCommentId) {
        return convertCommentsList(commentRepository.findAllByParentCommentId(parentCommentId));
    }

    private void validateUpdating(int updated) {
        if (updated == 1) return;
        throw new ApplicationException(ErrorCode.DATABASE_ERROR, "업데이트가 정상적으로 작동하지 않습니다.");
    }

    private Comment getExistentComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND, "존재하지 않는 Comment"));
    }

    private void validateCommentContext(CommentContext context) {
        Comment comment = getExistentComment(context.commentId());
        comment.validateOwner(context.userId());
        comment.validateQuestionId(context.questionId());
    }

    @Transactional
    public void updateComment(CommentRequestDto dto, CommentContext commentContext) {
        validateCommentContext(commentContext);
        validateUpdating(commentRepository.updateContent(commentContext.commentId(), dto.getContent()));
    }

    @Transactional
    public void deleteComment(CommentContext commentContext) {
        validateCommentContext(commentContext);

        validateUpdating(commentRepository.deleteById(commentContext.commentId()));
    }

    @Transactional
    public void likeComment(CommentContext context) {
        validateCommentContext(context);
        commentLikeService.addLikeComment(context.commentId(), context.userId());

        validateUpdating(commentRepository.updateUpVotedCount(context.commentId(), 1L));
    }

    @Transactional
    public void dislikeComment(CommentContext context) {
        validateCommentContext(context);
        commentLikeService.addDislikeComment(context.commentId(), context.userId());

        validateUpdating(commentRepository.updateDownVotedCount(context.commentId(), 1L));
    }

    @Transactional
    public void reportComment(ReportRequestDto dto, CommentContext context) {
        validateCommentContext(context);
        commentReportService.saveReport(context, dto);
        commentReportService.notifyAdminIfHighReports(context.commentId());

        validateUpdating(commentRepository.updateReportCount(context.commentId(), 1L));
    }
}
