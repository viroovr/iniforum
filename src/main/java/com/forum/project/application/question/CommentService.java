package com.forum.project.application.question;

import com.forum.project.application.converter.CommentDtoConverterFactory;
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

    @Transactional
    public ResponseCommentDto addComment(Long questionId, RequestCommentDto requestCommentDto, String accessToken) {
        Long userId = tokenService.getId(accessToken);
        Comment comment = CommentDtoConverterFactory.fromRequestDtoToEntity(requestCommentDto);

        comment.setQuestionId(questionId);
        comment.setUserId(userId);

        return CommentDtoConverterFactory.toResponseCommentDto(commentRepository.save(comment));
    }

    public List<ResponseCommentDto> getCommentsByQuestionId(Long questionId) {
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
    public ResponseCommentDto updateComment(Long id, RequestCommentDto requestCommentDto, String token) {
        String currentUserId = tokenService.getLoginId(token);
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));;

        if(!existingComment.getLoginId().equals(currentUserId)) {
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL);
        }

        existingComment.setContent(requestCommentDto.getContent());
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
