package com.forum.project.infrastructure.persistence.comment;

import com.forum.project.domain.comment.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    List<Comment> findByQuestionId(Long questionId);
    List<Comment> findByUserId(Long userId);
    List<Comment> findByParentCommentId(Long parentCommentId);

    Optional<Comment> findById(Long id);
    Comment save(Comment comment);
    Comment update(Comment comment);
    void deleteById(Long id);

}
