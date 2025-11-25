package com.school.sms.configuration.infrastructure.cache;

import com.school.sms.configuration.domain.model.Configuration;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caching service for configurations
 * Uses category-based caching with 1-hour TTL
 */
@Service
@RequiredArgsConstructor
public class ConfigurationCacheService {

    private static final String CACHE_NAME = "configurations";

    @Cacheable(value = CACHE_NAME, key = "#category")
    public List<Configuration> getByCategory(String category) {
        return null; // Cache miss - will be loaded from DB
    }

    public void cacheByCategory(String category, List<Configuration> configurations) {
        // Cached via @Cacheable in service methods
    }

    @CacheEvict(value = CACHE_NAME, key = "#category")
    public void evictByCategory(String category) {
        // Evict cache entry for specific category
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void evictAll() {
        // Clear all cache entries
    }
}
