package com.forum.project.domain.question.service;

import com.forum.project.domain.question.repository.QuestionRepository;
import com.forum.project.domain.question.entity.ViewCount;
import com.forum.project.domain.question.repository.ViewCountStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class QuestionViewCountService {
    private final ViewCountStore viewCountStore;
    private final QuestionRepository questionRepository;

    private String generateCacheKey(Long questionId, Long userId) {
        return questionId + ":" + userId;
    }

    public Long getViewCount(Long questionId, Long userId) {
        String key = generateCacheKey(questionId, userId);
        ViewCount viewCount = viewCountStore.getViewCount(key);
        if (!viewCountStore.hasKey(key)) {
            Long viewCountValue = questionRepository.getViewCountById(questionId);
            viewCountStore.setViewCount(key, viewCountValue);
        }
        return viewCount.getCount();
    }

    public void incrementViewCount(Long questionId, Long userId) {
        String key = generateCacheKey(questionId, userId);
        if (!viewCountStore.hasKey(key)) {
            Long dbViewCount = questionRepository.getViewCountById(questionId);
            viewCountStore.setViewCount(key, dbViewCount + 1L);
        } else
            viewCountStore.increment(key);
    }

    public void decrementViewCount(Long questionId, Long userId) {
        String key = generateCacheKey(questionId, userId);

        if (!viewCountStore.hasKey(key)) {
            Long dbViewCount = questionRepository.getViewCountById(questionId);
            viewCountStore.setViewCount(key, dbViewCount - 1L);
        } else {
            viewCountStore.decrement(key);
        }
    }

    @Transactional
    public void syncToDatabase() {
        Set<String> keys = viewCountStore.getAllKeys();

        for (String key : keys) {
            String[] parts = key.split(":");
            Long questionId = Long.valueOf(parts[2]);

            ViewCount viewCount = viewCountStore.getViewCount(key);

            questionRepository.updateViewCount(questionId, viewCount.getCount());
        }

        viewCountStore.clearAllKeys();
    }
}
