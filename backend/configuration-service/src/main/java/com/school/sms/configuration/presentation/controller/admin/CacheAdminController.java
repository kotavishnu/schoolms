package com.school.sms.configuration.presentation.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Cache administration REST controller for Configuration Service.
 *
 * Provides administrative endpoints to manage and monitor Redis cache.
 *
 * Security Note: These endpoints should be secured with authentication in production.
 * Current implementation is for development/testing purposes only.
 *
 * @author SMS Backend Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@RestController
@RequestMapping("/api/v1/admin/cache")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cache Admin", description = "Cache administration and management endpoints")
public class CacheAdminController {

    private final CacheManager cacheManager;

    /**
     * Clear a specific cache by name.
     *
     * @param cacheName Name of cache to clear
     * @return Success message
     */
    @PostMapping("/clear/{cacheName}")
    @Operation(summary = "Clear specific cache", description = "Clears all entries from the specified cache")
    public ResponseEntity<Map<String, Object>> clearCache(@PathVariable String cacheName) {
        log.info("Admin request to clear cache: {}", cacheName);

        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("Cache not found: {}", cacheName);
            return ResponseEntity.notFound().build();
        }

        cache.clear();
        log.info("Successfully cleared cache: {}", cacheName);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Cache cleared successfully",
                "cacheName", cacheName,
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Clear all caches.
     *
     * @return Success message with count of cleared caches
     */
    @PostMapping("/clear-all")
    @Operation(summary = "Clear all caches", description = "Clears all entries from all caches")
    public ResponseEntity<Map<String, Object>> clearAllCaches() {
        log.info("Admin request to clear all caches");

        Collection<String> cacheNames = cacheManager.getCacheNames();
        int clearedCount = 0;

        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                clearedCount++;
                log.debug("Cleared cache: {}", cacheName);
            }
        }

        log.info("Successfully cleared {} caches", clearedCount);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "All caches cleared successfully",
                "clearedCount", clearedCount,
                "cacheNames", cacheNames,
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Get cache statistics.
     *
     * @return Cache statistics for all caches
     */
    @GetMapping("/stats")
    @Operation(summary = "Get cache statistics", description = "Returns statistics for all caches")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        log.info("Admin request for cache statistics");

        Collection<String> cacheNames = cacheManager.getCacheNames();
        List<Map<String, Object>> cacheStats = new ArrayList<>();

        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Map<String, Object> stats = new HashMap<>();
                stats.put("name", cacheName);
                stats.put("nativeCache", cache.getNativeCache().getClass().getSimpleName());
                stats.put("type", cache.getClass().getSimpleName());
                cacheStats.add(stats);
            }
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "totalCaches", cacheNames.size(),
                "caches", cacheStats,
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * List all cache names.
     *
     * @return List of all configured cache names
     */
    @GetMapping("/names")
    @Operation(summary = "List cache names", description = "Returns list of all configured cache names")
    public ResponseEntity<Map<String, Object>> getCacheNames() {
        log.info("Admin request for cache names");

        Collection<String> cacheNames = cacheManager.getCacheNames();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "count", cacheNames.size(),
                "cacheNames", cacheNames.stream().sorted().collect(Collectors.toList()),
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Health check for cache system.
     *
     * @return Health status of cache system
     */
    @GetMapping("/health")
    @Operation(summary = "Cache health check", description = "Returns health status of cache system")
    public ResponseEntity<Map<String, Object>> getCacheHealth() {
        log.info("Admin request for cache health check");

        boolean allHealthy = true;
        List<Map<String, Object>> cacheHealth = new ArrayList<>();

        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            boolean isHealthy = cache != null;

            cacheHealth.add(Map.of(
                    "name", cacheName,
                    "status", isHealthy ? "UP" : "DOWN"
            ));

            if (!isHealthy) {
                allHealthy = false;
            }
        }

        return ResponseEntity.ok(Map.of(
                "status", allHealthy ? "UP" : "DOWN",
                "overallHealth", allHealthy,
                "caches", cacheHealth,
                "timestamp", System.currentTimeMillis()
        ));
    }
}
