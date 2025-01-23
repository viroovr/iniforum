package com.forum.project.domain.comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    List<Comment> findAllByQuestionId(Long questionId);
    List<Comment> findAllByUserId(Long userId);
    List<Comment> findAllByParentCommentId(Long parentCommentId);

    Optional<Comment> findById(Long id);
    Comment insert(Comment comment);
    void updateContent(Long id, String content);
    void deleteById(Long id);

    boolean existsById(Long commentId);
}
