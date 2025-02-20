package com.forum.project.infrastructure.persistence.repository;

import com.forum.project.domain.user.entity.UserActivityLog;
import com.forum.project.domain.user.repository.UserActivityLogRepository;
import org.springframework.stereotype.Repository;

@Repository
public class tempUserActivityLogRepositoryImpl implements UserActivityLogRepository {
    @Override
    public void save(UserActivityLog log) {
        return;
    }
}
