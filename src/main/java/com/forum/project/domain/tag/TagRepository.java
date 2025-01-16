package com.forum.project.domain.tag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TagRepository {
    List<Tag> findByNameContainingIgnoreCase(String keyword);
    boolean existsByName(String name);
    Tag save(Tag tag);

    List<Tag> findAll();
    List<Tag> findAllById(List<Long> tagIds);

    Optional<Tag> findById(Long id);

    Optional<Tag> findByName(String name);

    List<Tag> findTagsByQuestionId(Long id);

    List<Tag> findAllByName(List<String> tagNames);

    Collection<Object> findAllByNameIn(List<String> tagNames);

    List<Tag> saveAll(List<Tag> newTags);
}
