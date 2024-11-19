package com.forum.project.domain.repository;

import com.forum.project.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByQuestionId(Long questionId);

}
