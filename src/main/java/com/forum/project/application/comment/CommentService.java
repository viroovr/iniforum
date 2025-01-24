package com.forum.project.application.comment;

import com.forum.project.application.user.UserProfileService;
import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.domain.comment.Comment;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.comment.CommentRepository;
import com.forum.project.presentation.report.ReportRequestDto;
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
    private final UserProfileService userProfileService;

    @Transactional
    public CommentResponseDto addComment(Long questionId, Long userId, CommentRequestDto commentRequestDto) {
        Comment comment = CommentDtoConverterFactory.fromRequestDtoToEntity(commentRequestDto);
        comment.setQuestionId(questionId);
        comment.setUserId(userId);
        comment.setLoginId(userProfileService.getLoginId(userId));
        comment.setKeys(commentRepository.insertAndReturnGeneratedKeys(comment));
        return CommentDtoConverterFactory.toResponseCommentDto(comment);
    }

    public List<CommentResponseDto> getCommentsByQuestionId(Long questionId) {
        return commentRepository.findAllByQuestionId(questionId)
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
    public void updateComment(Long commentId, Long userId, CommentRequestDto commentRequestDto) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));

        if (!existingComment.getUserId().equals(userId))
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL);

        existingComment.updateContent(commentRequestDto.getContent());
        commentRepository.updateContent(commentId, existingComment.getContent());
    }

    @Transactional
    public void likeComment(Long commentId, Long userId) {
        commentLikeService.addLikeComment(commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));
        comment.increaseUpVote();
        commentRepository.updateUpVotedCount(commentId, comment.getUpVotedCount());
    }

    @Transactional
    public void dislikeComment(Long commentId, Long userId) {
        commentLikeService.addDislikeComment(commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));
        comment.increaseDownVote();
        commentRepository.updateDownVotedCount(commentId, comment.getDownVotedCount());
    }

    @Transactional
    public void reportComment(Long commentId, Long userId, ReportRequestDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));
        comment.increaseReportCount();
        commentReportService.saveReport(commentId, userId, dto.getReason());
        commentReportService.notifyAdminIfHighReports(commentId);
    }

    @AuthCheck
    public List<CommentResponseDto> getUserComments(Long userId, String header) {
        return commentRepository.findAllByUserId(userId)
                .stream()
                .map(CommentDtoConverterFactory::toResponseCommentDto)
                .toList();
    }

    @Transactional
    public CommentResponseDto replyToComment(Long parentCommentId, Long userId, CommentRequestDto commentRequestDto) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));

        Comment replyComment = CommentDtoConverterFactory.fromRequestDtoToEntity(commentRequestDto);
        replyComment.setQuestionId(parentComment.getQuestionId());
        replyComment.setUserId(userId);
        replyComment.setLoginId(userProfileService.getLoginId(userId));
        replyComment.setParentCommentId(parentCommentId);
        replyComment.setKeys(commentRepository.insertAndReturnGeneratedKeys(replyComment));
        return CommentDtoConverterFactory.toResponseCommentDto(replyComment);
    }

    public List<CommentResponseDto> getChildComments(Long parentCommentId) {
        return commentRepository.findAllByParentCommentId(parentCommentId)
                .stream()
                .map(CommentDtoConverterFactory::toResponseCommentDto)
                .toList();
    }
}
