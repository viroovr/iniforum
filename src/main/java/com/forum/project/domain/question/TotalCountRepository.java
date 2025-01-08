package com.forum.project.domain.question;

public interface TotalCountRepository {
    Long getTotalCount();
    void setTotalCount(Long count);
    void incrementTotalCount();
    void decrementTotalCount();
}
