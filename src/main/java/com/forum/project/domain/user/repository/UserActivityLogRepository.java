package com.forum.project.domain.user.repository;

import com.forum.project.domain.user.entity.UserActivityLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserActivityLogRepository extends ElasticsearchRepository<UserActivityLog, String> {
}
