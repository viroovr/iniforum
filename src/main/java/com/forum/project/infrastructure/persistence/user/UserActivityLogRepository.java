package com.forum.project.infrastructure.persistence.user;

import com.forum.project.domain.user.UserActivityLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityLogRepository extends ElasticsearchRepository<UserActivityLog, String> {
}
