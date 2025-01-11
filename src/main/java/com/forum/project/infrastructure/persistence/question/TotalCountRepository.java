package com.forum.project.infrastructure.persistence.question;

public interface TotalCountRepository {
    Long getTotalCount();
    void setTotalCount(Long count);
    void incrementTotalCount();
    void decrementTotalCount();
}
