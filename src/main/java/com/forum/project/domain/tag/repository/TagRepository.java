package com.forum.project.domain.tag.repository;

import com.forum.project.domain.tag.entity.Tag;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TagRepository {

    Map<String, Object> insertAndReturnGeneratedKeys(Tag tag);
    List<Map<String, Object>> saveAll(List<Tag> tags);

    Optional<Tag> findById(Long id);
    Optional<Tag> findByName(String name);
    List<Tag> findByIds(List<Long> tagIds);
    List<Tag> findTagsByQuestionId(Long questionId);
    List<Tag> findByNames(List<String> tagNames);
    List<Tag> getByPage(int page, int size);
    List<Tag> searchByName(String keyword, int page, int size);
    List<Tag> searchByNames(List<String> tagNames, int page, int size);

    boolean existsByName(String name);

    int updateName(Long id, String name);

    void delete(Long id);
}
