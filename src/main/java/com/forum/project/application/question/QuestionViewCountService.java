package com.forum.project.application.question;

import com.forum.project.domain.question.QuestionRepository;
import com.forum.project.domain.question.ViewCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class QuestionViewCountService {

    private final ViewCountRepository viewCountRepository;

    private final QuestionRepository questionRepository;

    private String generateCacheKey(Long questionId, Long userId) {
        return questionId + ":" + userId;
    }

    public Long getViewCount(Long questionId, Long userId) {
        String key = generateCacheKey(questionId, userId);
        Long viewCount = viewCountRepository.getViewCount(key);
        if (!viewCountRepository.hasKey(key)) {
            viewCount = questionRepository.getViewCountByQuestionId(questionId);
            viewCountRepository.setViewCount(key, viewCount);
        }
        return viewCount;
    }

    public void incrementViewCount(Long questionId, Long userId) {
        String key = generateCacheKey(questionId, userId);
        if (!viewCountRepository.hasKey(key)) {
            Long dbViewCount = questionRepository.getViewCountByQuestionId(questionId);
            viewCountRepository.setViewCount(key, dbViewCount + 1L);
        } else
            viewCountRepository.increment(key);
    }

    public void decrementViewCount(Long questionId, Long userId) {
        String key = generateCacheKey(questionId, userId);

        if (!viewCountRepository.hasKey(key)) {
            Long dbViewCount = questionRepository.getViewCountByQuestionId(questionId);
            viewCountRepository.setViewCount(key, dbViewCount - 1L);
        } else {
            viewCountRepository.decrement(key);
        }
    }

    @Transactional
    public void syncToDatabase() {
        Set<String> keys = viewCountRepository.getAllKeys();

        for (String key : keys) {
            String[] parts = key.split(":");
            Long questionId = Long.valueOf(parts[2]);

            Long viewCount = viewCountRepository.getViewCount(key);

            questionRepository.updateViewCount(questionId, viewCount);
        }

        viewCountRepository.clearAllKeys();
    }
}
