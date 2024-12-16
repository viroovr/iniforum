package com.forum.project.domain.repository;

import com.forum.project.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    List<Comment> findByQuestionId(Long questionId);
    Optional<Comment> findById(Long id);
    Comment save(Comment comment);
    Comment update(Comment comment);
    void deleteById(Long id);
}
