package com.forum.project.domain.comment.repository;

import com.forum.project.domain.comment.entity.Comment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentRepository {
    Map<String, Object> insertAndReturnGeneratedKeys(Comment comment);

    List<Comment> findAllByQuestionId(Long questionId);
    List<Comment> findAllByUserId(Long userId);
    List<Comment> findAllByParentCommentId(Long parentCommentId);
    Optional<Comment> findById(Long id);
    boolean existsById(Long id);

    int updateContent(Long id, String content);
    int updateDownVotedCount(Long id, Long downVotedCount);
    int updateUpVotedCount(Long id, Long upVotedCount);

    void deleteById(Long id);


}
