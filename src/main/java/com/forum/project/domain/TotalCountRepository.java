package com.forum.project.domain;

public interface TotalCountRepository {
    Long getTotalCount();
    void setTotalCount(Long count);
    void incrementTotalCount();
    void decrementTotalCount();
}
