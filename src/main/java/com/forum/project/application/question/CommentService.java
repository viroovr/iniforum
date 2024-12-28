package com.forum.project.application.question;

import com.forum.project.application.converter.CommentDtoConverterFactory;
import com.forum.project.application.jwt.TokenService;
import com.forum.project.domain.comment.Comment;
import com.forum.project.domain.commentlike.CommentLike;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.commentlike.CommentLikeRepository;
import com.forum.project.domain.comment.CommentRepository;
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
    private final CommentLikeRepository commentLikeRepository;
    private final TokenService tokenService;

    @Transactional
    public CommentResponseDto addComment(Long questionId, CommentRequestDto commentRequestDto, String accessToken) {
        Long userId = tokenService.getId(accessToken);
        Comment comment = CommentDtoConverterFactory.fromRequestDtoToEntity(commentRequestDto);

        comment.setQuestionId(questionId);
        comment.setUserId(userId);

        return CommentDtoConverterFactory.toResponseCommentDto(commentRepository.save(comment));
    }

    public List<CommentResponseDto> getCommentsByQuestionId(Long questionId) {
        return commentRepository.findByQuestionId(questionId)
                .stream().map(CommentDtoConverterFactory::toResponseCommentDto).toList();
    }

    @Transactional
    public void deleteComment(Long commentId, String token) {
        String currentLoginId = tokenService.getLoginId(token);
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));

        if(!existingComment.getLoginId().equals(currentLoginId)) {
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL);
        }
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto commentRequestDto, String token) {
        String currentUserId = tokenService.getLoginId(token);
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));;

        if(!existingComment.getLoginId().equals(currentUserId)) {
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL);
        }

        existingComment.setContent(commentRequestDto.getContent());
        return CommentDtoConverterFactory.toResponseCommentDto(commentRepository.save(existingComment));
    }

    @Transactional
    public void likeComment(Long commentId, String token) {
        Long userId = tokenService.getId(token);

        if(commentLikeRepository.existsByCommentIdAndUserId(commentId, userId))
            throw new ApplicationException(ErrorCode.LIKE_ALREADY_EXISTS);

        commentLikeRepository.save(CommentLike.builder()
                        .userId(userId)
                        .commentId(commentId)
                .build());
    }
}
