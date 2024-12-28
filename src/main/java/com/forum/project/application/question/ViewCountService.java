package com.forum.project.application.question;

import com.forum.project.domain.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ViewCountService {

    private final StringRedisTemplate redisTemplate;

    private final QuestionRepository questionRepository;

    private final String REDIS_PREFIX = "ViewCount:";

    public void incrementViewCount(Long questionId, String userId) {
        String key = REDIS_PREFIX + questionId + ":" + userId;
        redisTemplate.opsForValue().increment(key, 1);
    }

    public void handleView(Long questionId, String userId) {
        String key = REDIS_PREFIX + questionId + ":" + userId;

        Boolean isViewed = redisTemplate.hasKey(key);
        if (Boolean.FALSE.equals(isViewed)) {
            redisTemplate.opsForValue().set(key, "1", 5, TimeUnit.MINUTES);
        } else {
            incrementViewCount(questionId, userId);
        }
    }

    @Transactional
    public void syncToDatabase() {
        Set<String> keys = redisTemplate.keys(REDIS_PREFIX + "*");
        if (keys != null) {
            for (String key: keys) {
                Long questionId = Long.valueOf(key.split(":")[1]);
                Integer viewCount = Integer.valueOf(Objects.requireNonNull(redisTemplate.opsForValue().get(key)));

                questionRepository.updateViewCount(questionId, viewCount);
            }
        }
    }
}
