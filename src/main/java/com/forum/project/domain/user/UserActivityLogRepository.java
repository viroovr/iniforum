package com.forum.project.domain.user;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityLogRepository extends ElasticsearchRepository<UserActivityLog, String> {
}
