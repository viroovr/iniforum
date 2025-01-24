package com.forum.project.domain.comment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentRepository {
    List<Comment> findAllByQuestionId(Long questionId);
    List<Comment> findAllByUserId(Long userId);
    List<Comment> findAllByParentCommentId(Long parentCommentId);

    Optional<Comment> findById(Long id);
    Map<String, Object> insertAndReturnGeneratedKeys(Comment comment);
    void updateContent(Long id, String content);
    void deleteById(Long id);

    boolean existsById(Long id);

    void updateDownVotedCount(Long id, Long downVotedCount);

    void updateUpVotedCount(Long id, Long upVotedCount);
}
