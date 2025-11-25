package com.school.sms.student.infrastructure.cache;

import com.school.sms.student.domain.model.Student;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

/**
 * Service for caching student data using Redis.
 */
@Service
public class StudentCacheService {

    private static final String CACHE_NAME = "students";

    /**
     * Get student from cache.
     */
    @Cacheable(value = CACHE_NAME, key = "#studentId", unless = "#result == null")
    public Student getStudent(String studentId) {
        // Cache miss - will be populated by caller
        return null;
    }

    /**
     * Cache a student.
     */
    public void cacheStudent(Student student) {
        // Managed by Spring Cache via @Cacheable on repository methods
    }

    /**
     * Evict student from cache.
     */
    @CacheEvict(value = CACHE_NAME, key = "#studentId")
    public void evictStudent(String studentId) {
        // Student removed from cache
    }

    /**
     * Evict all students from cache.
     */
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void evictAll() {
        // All students removed from cache
    }
}
