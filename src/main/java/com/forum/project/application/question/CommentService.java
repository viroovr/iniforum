package com.forum.project.application.question;

import com.forum.project.application.converter.CommentDtoConverter;
import com.forum.project.application.security.jwt.TokenService;
import com.forum.project.domain.entity.Comment;
import com.forum.project.domain.entity.CommentLike;
import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.ErrorCode;
import com.forum.project.domain.repository.CommentLikeRepository;
import com.forum.project.domain.repository.CommentRepository;
import com.forum.project.presentation.dtos.comment.RequestCommentDto;
import com.forum.project.presentation.dtos.comment.ResponseCommentDto;
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
    private final CommentDtoConverter converter;

    @Transactional
    public ResponseCommentDto addComment(Long questionId, RequestCommentDto requestCommentDto, String accessToken) {
        String userId = tokenService.getUserId(accessToken);
        Comment comment = converter.fromRequestDtoToEntity(requestCommentDto);

        comment.setQuestionId(questionId);
        comment.setUserId(userId);

        return converter.toResponseCommentDto(commentRepository.save(comment));
    }

    public List<ResponseCommentDto> getCommentsByQuestionId(Long questionId) {
        return commentRepository.findByQuestionId(questionId)
                .stream().map(converter::toResponseCommentDto).toList();
    }

    @Transactional
    public void deleteComment(Long commentId, String token) {
        String currentUserId = tokenService.getUserId(token);
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));

        if(!existingComment.getUserId().equals(currentUserId)) {
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL);
        }
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public ResponseCommentDto updateComment(Long id, RequestCommentDto requestCommentDto, String token) {
        String currentUserId = tokenService.getUserId(token);
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));;

        if(!existingComment.getUserId().equals(currentUserId)) {
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL);
        }

        existingComment.setContent(requestCommentDto.getContent());
        return converter.toResponseCommentDto(commentRepository.save(existingComment));
    }

    @Transactional
    public void likeComment(Long commentId, String token) {
        Long userId = tokenService.getId(token);

        if(commentLikeRepository.existsByCommentIdAndUserId(commentId, userId))
            throw new ApplicationException(ErrorCode.LIKE_ALREADY_EXISTS);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));

        commentLikeRepository.save(new CommentLike(userId, commentId));
    }
}
