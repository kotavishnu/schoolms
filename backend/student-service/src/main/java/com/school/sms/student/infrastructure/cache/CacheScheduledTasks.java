package com.school.sms.student.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Scheduled cache cleanup tasks for Student Service.
 *
 * Implements periodic cache cleanup as per LESSONS_LEARNED.md [D-005]
 *
 * Cleanup Schedule:
 * - Search results cache: Cleared every hour (prevents stale search data)
 * - All stale entries: Daily cleanup at 2 AM
 *
 * @author SMS Backend Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class CacheScheduledTasks {

    private final CacheManager cacheManager;

    /**
     * Clear search results cache every hour.
     *
     * Search results are dynamic and can become stale quickly.
     * This prevents users from seeing outdated search results.
     *
     * Schedule: Every hour at minute 0 (0 0 * * * *)
     */
    @Scheduled(cron = "0 0 * * * *")
    public void clearSearchResultsCache() {
        try {
            var cache = cacheManager.getCache("studentSearchResults");
            if (cache != null) {
                cache.clear();
                log.info("Cleared studentSearchResults cache - scheduled hourly cleanup");
            }
        } catch (Exception e) {
            log.error("Failed to clear studentSearchResults cache: {}", e.getMessage(), e);
        }
    }

    /**
     * Clear active students count cache every 30 minutes.
     *
     * Count caches can become stale as students are added/removed.
     *
     * Schedule: Every 30 minutes (0 0/30 * * * *)
     */
    @Scheduled(cron = "0 0/30 * * * *")
    public void clearCountCache() {
        try {
            var cache = cacheManager.getCache("activeStudentsCount");
            if (cache != null) {
                cache.clear();
                log.info("Cleared activeStudentsCount cache - scheduled 30-minute cleanup");
            }
        } catch (Exception e) {
            log.error("Failed to clear activeStudentsCount cache: {}", e.getMessage(), e);
        }
    }

    /**
     * Comprehensive cache cleanup - runs daily at 2 AM.
     *
     * Clears all caches to ensure fresh data and prevent memory bloat.
     * Scheduled during low-traffic hours to minimize impact.
     *
     * Schedule: Daily at 2:00 AM (0 0 2 * * *)
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void clearAllStaleEntries() {
        try {
            log.info("Starting daily cache cleanup at 2 AM");

            int clearedCount = 0;
            for (String cacheName : cacheManager.getCacheNames()) {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cache.clear();
                    clearedCount++;
                    log.debug("Cleared cache: {}", cacheName);
                }
            }

            log.info("Completed daily cache cleanup - cleared {} caches", clearedCount);

        } catch (Exception e) {
            log.error("Failed to perform daily cache cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Log cache statistics every 15 minutes for monitoring.
     *
     * Provides visibility into cache usage patterns.
     *
     * Schedule: Every 15 minutes (0 0/15 * * * *)
     */
    @Scheduled(cron = "0 0/15 * * * *")
    public void logCacheStatistics() {
        try {
            log.info("=== Cache Statistics ===");
            for (String cacheName : cacheManager.getCacheNames()) {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    log.info("Cache [{}] - Native: {}", cacheName, cache.getNativeCache().getClass().getSimpleName());
                }
            }
            log.info("========================");
        } catch (Exception e) {
            log.error("Failed to log cache statistics: {}", e.getMessage(), e);
        }
    }
}
