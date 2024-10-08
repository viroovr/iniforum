package com.forum.project.application;

import com.forum.project.application.security.jwt.JwtTokenProvider;
import com.forum.project.domain.*;
import com.forum.project.presentation.comment.RequestCommentDto;
import com.forum.project.presentation.comment.ResponseCommentDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ResponseCommentDto addComment(Long questionId, RequestCommentDto requestCommentDto) {
        Question question = new Question();
        question.setId(questionId);

        Comment comment = RequestCommentDto.toEntity(requestCommentDto);
        comment.setQuestion(question);
        comment.setLikeCount(0L);

        return ResponseCommentDto.toDto(commentRepository.save(comment));
    }

    public List<ResponseCommentDto> getCommentsByQuestionId(Long questionId) {
        return commentRepository.findByQuestionId(questionId)
                .stream().map(ResponseCommentDto::toDto).toList();
    }

    @Transactional
    public void deleteComment(Long id, String token) {

        String currentUserId = jwtTokenProvider.getUserId(token);
        Optional<Comment> existingComment = commentRepository.findById(id);

        if(existingComment.isPresent()) {
            Comment existing = existingComment.get();
            if(!existing.getUserId().equals(currentUserId)) {
                throw new BadCredentialsException("You are not authorized");
            }
            commentRepository.delete(existing);
            return ;
        }

        throw new EntityNotFoundException("Comment not found.");


    }

    @Transactional
    public ResponseCommentDto updateComment(Long id, RequestCommentDto requestCommentDto, String token) {
        String currentUserId = jwtTokenProvider.getUserId(token);
        Optional<Comment> existingComment = commentRepository.findById(id);

        if (existingComment.isPresent()) {
            Comment existing = existingComment.get();

            if(!existing.getUserId().equals(currentUserId)) {
                throw new BadCredentialsException("You are not authorized");
            }

            existing.setContent(requestCommentDto.getContent());
            return ResponseCommentDto.toDto(commentRepository.save(existing));
        } else {
            throw new EntityNotFoundException("Comment not found.");

        }
    }

    @Transactional
    public void likeComment(Long commentId, String token) {
        String userId = jwtTokenProvider.getUserId(token);

        if(commentLikeRepository.existsByCommentIdAndUserId(commentId, userId))
            throw new IllegalArgumentException("Already recommend");

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        commentLikeRepository.save(new CommentLike(userId, comment));

        AtomicLong atomicLong = new AtomicLong(comment.getLikeCount());
        comment.setLikeCount(atomicLong.addAndGet(1));

        commentRepository.save(comment);
    }

}
