package com.forum.project.application.comment;

import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.domain.comment.Comment;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.comment.CommentRepository;
import com.forum.project.domain.commentlike.ReportRequestDto;
import com.forum.project.infrastructure.security.auth.AuthCheck;
import com.forum.project.presentation.comment.CommentRequestDto;
import com.forum.project.presentation.comment.CommentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final AuthenticationService authenticationService;
    private final CommentLikeService commentLikeService;
    private final CommentReportService commentReportService;

    @Transactional
    @AuthCheck
    public CommentResponseDto addComment(Long questionId, CommentRequestDto commentRequestDto, String header) {
        Long userId = authenticationService.extractUserId(header);
        String loginId = authenticationService.extractLoginId(header);
        Comment comment = CommentDtoConverterFactory.fromRequestDtoToEntity(commentRequestDto);

        comment.initialize(questionId, userId, loginId);

        return CommentDtoConverterFactory.toResponseCommentDto(commentRepository.save(comment));
    }

    public List<CommentResponseDto> getCommentsByQuestionId(Long questionId) {
        return commentRepository.findByQuestionId(questionId)
                .stream().map(CommentDtoConverterFactory::toResponseCommentDto).toList();
    }

    @Transactional
    @AuthCheck
    public void deleteComment(Long commentId, String header) {
        Long currentUserId = authenticationService.extractUserId(header);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUserId().equals(currentUserId))
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL);

        commentRepository.deleteById(commentId);
    }

    @Transactional
    @AuthCheck
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto commentRequestDto, String header) {
        Long currentUserId = authenticationService.extractUserId(header);
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));

        if (!existingComment.getUserId().equals(currentUserId))
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL);

        existingComment.updateContent(commentRequestDto.getContent());

        return CommentDtoConverterFactory.toResponseCommentDto(commentRepository.update(existingComment));
    }

    @Transactional
    public void likeComment(Long commentId, String header) {
        Long userId = authenticationService.extractUserId(header);

        commentLikeService.addLikeComment(commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));
        comment.increaseUpVote();
        commentRepository.save(comment);
    }

    @Transactional
    public void dislikeComment(Long commentId, String header) {
        Long userId = authenticationService.extractUserId(header);

        commentLikeService.addDislikeComment(commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));
        comment.increaseDownVote();
        commentRepository.save(comment);
    }

    @Transactional
    public void reportComment(Long commentId, String header, ReportRequestDto dto) {
        Long userId = authenticationService.extractUserId(header);
        if (!commentRepository.existsById(commentId))
            throw new ApplicationException(ErrorCode.COMMENT_NOT_FOUND);

        commentReportService.saveReport(commentId, userId, dto.getReason());
        commentReportService.notifyAdminIfHighReports(commentId);
    }

    @AuthCheck
    public List<CommentResponseDto> getUserComments(Long userId, String header) {
        return commentRepository.findByUserId(userId)
                .stream()
                .map(CommentDtoConverterFactory::toResponseCommentDto)
                .toList();
    }

    @Transactional
    public CommentResponseDto replyToComment(Long parentCommentId, CommentRequestDto commentRequestDto, String header) {
        Long userId = authenticationService.extractUserId(header);
        String loginId = authenticationService.extractLoginId(header);
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));

        Comment replyComment = CommentDtoConverterFactory.fromRequestDtoToEntity(commentRequestDto);
        replyComment.initialize(parentComment.getQuestionId(), userId, loginId);
        replyComment.setParentCommentId(parentCommentId);

        return CommentDtoConverterFactory.toResponseCommentDto(commentRepository.save(replyComment));
    }

    public List<CommentResponseDto> getChildComments(Long parentCommentId) {
        return commentRepository.findByParentCommentId(parentCommentId)
                .stream()
                .map(CommentDtoConverterFactory::toResponseCommentDto)
                .toList();
    }
}
