package com.school.sms.configuration.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled cache cleanup tasks for Configuration Service.
 *
 * Implements periodic cache cleanup as per LESSONS_LEARNED.md [D-005]
 *
 * Cleanup Schedule:
 * - Daily cleanup at 3 AM (offset from student service to spread load)
 *
 * Configuration data is more stable than student data, so less frequent cleanup is needed.
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
     * Comprehensive cache cleanup - runs daily at 3 AM.
     *
     * Configuration data is stable, but periodic cleanup ensures:
     * - Fresh data after configuration updates
     * - Prevents memory bloat
     * - Removes any orphaned cache entries
     *
     * Schedule: Daily at 3:00 AM (0 0 3 * * *)
     * Offset from student service (2 AM) to distribute load
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void clearAllStaleEntries() {
        try {
            log.info("Starting daily cache cleanup at 3 AM");

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
     * Log cache statistics every 30 minutes for monitoring.
     *
     * Provides visibility into cache usage patterns.
     * Less frequent than student service (15 min) due to lower traffic.
     *
     * Schedule: Every 30 minutes (0 0/30 * * * *)
     */
    @Scheduled(cron = "0 0/30 * * * *")
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
