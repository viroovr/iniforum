package com.forum.project.domain.repository;

public interface TotalCountRepository {
    Long getTotalCount();
    void setTotalCount(Long count);
    void incrementTotalCount();
    void decrementTotalCount();
}
