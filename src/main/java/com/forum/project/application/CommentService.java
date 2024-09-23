package com.forum.project.application;

import com.forum.project.domain.Comment;
import com.forum.project.domain.CommentRepository;
import com.forum.project.domain.Question;
import com.forum.project.presentation.comment.RequestCommentDto;
import com.forum.project.presentation.comment.ResponseCommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

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

}
