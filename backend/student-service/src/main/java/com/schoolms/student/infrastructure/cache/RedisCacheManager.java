package com.schoolms.student.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;

/**
 * Redis Cache Manager
 * Manages caching operations for student data
 */
@Component
public class RedisCacheManager {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheManager.class);
    private static final String STUDENT_KEY_PREFIX = "sms:student:";
    private static final String STUDENT_LIST_KEY = "sms:students:all";
    private static final String STATISTICS_KEY = "sms:students:statistics";
    private static final Duration STUDENT_TTL = Duration.ofHours(2);
    private static final Duration LIST_TTL = Duration.ofMinutes(10);
    private static final Duration STATS_TTL = Duration.ofMinutes(5);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheManager(
        RedisTemplate<String, Object> redisTemplate,
        ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Cache student response
     */
    public void cacheStudent(String studentId, Object student) {
        try {
            String key = STUDENT_KEY_PREFIX + studentId;
            redisTemplate.opsForValue().set(key, student, STUDENT_TTL);
            log.debug("Cached student: studentId={}", studentId);
        } catch (Exception e) {
            log.error("Failed to cache student: studentId={}", studentId, e);
        }
    }

    /**
     * Get cached student
     */
    public <T> T getStudent(String studentId, Class<T> type) {
        try {
            String key = STUDENT_KEY_PREFIX + studentId;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                return objectMapper.convertValue(cached, type);
            }
        } catch (Exception e) {
            log.error("Failed to retrieve student from cache: studentId={}", studentId, e);
        }
        return null;
    }

    /**
     * Evict student cache
     */
    public void evictStudent(String studentId) {
        try {
            String key = STUDENT_KEY_PREFIX + studentId;
            redisTemplate.delete(key);
            log.debug("Evicted student cache: studentId={}", studentId);
        } catch (Exception e) {
            log.error("Failed to evict student cache: studentId={}", studentId, e);
        }
    }

    /**
     * Evict all student-related caches
     */
    public void evictAllStudentCaches() {
        try {
            redisTemplate.delete(STUDENT_LIST_KEY);
            redisTemplate.delete(STATISTICS_KEY);
            log.debug("Evicted all student list and statistics caches");
        } catch (Exception e) {
            log.error("Failed to evict student list caches", e);
        }
    }
}
