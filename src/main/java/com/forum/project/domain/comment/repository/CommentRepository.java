package com.forum.project.domain.comment.repository;

import com.forum.project.domain.comment.dto.CommentCreateDto;
import com.forum.project.domain.comment.entity.Comment;
import com.forum.project.infrastructure.persistence.key.CommentKey;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentRepository {
    Optional<CommentKey> insertAndReturnGeneratedKeys(CommentCreateDto dto);

    List<Comment> findAllByQuestionId(Long questionId);
    List<Comment> findAllByUserId(Long userId);
    List<Comment> findAllByParentCommentId(Long parentCommentId);
    Optional<Comment> findById(Long id);
    boolean existsById(Long id);

    int updateContent(Long id, String content);
    int updateDownVotedCount(Long id, Long delta);
    int updateUpVotedCount(Long id, Long delta);
    int updateReportCount(Long id, Long delta);

    int deleteById(Long id);


}
