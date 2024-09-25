package com.forum.project.application;

import com.forum.project.application.security.jwt.JwtTokenProvider;
import com.forum.project.domain.Comment;
import com.forum.project.domain.CommentRepository;
import com.forum.project.domain.Question;
import com.forum.project.presentation.comment.RequestCommentDto;
import com.forum.project.presentation.comment.ResponseCommentDto;
import com.forum.project.presentation.question.RequestQuestionDto;
import com.forum.project.presentation.question.ResponseQuestionDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ResponseCommentDto addComment(Long questionId, RequestCommentDto requestCommentDto) {
        Question question = new Question();
        question.setId(questionId);

        Comment comment = RequestCommentDto.toEntity(requestCommentDto);
        comment.setQuestion(question);

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
        } else {
            throw new EntityNotFoundException("Comment not found.");
        }


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

}
