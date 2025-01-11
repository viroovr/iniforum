package com.forum.project.domain.comment;

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

    void hideComment(Long commentId);
}
