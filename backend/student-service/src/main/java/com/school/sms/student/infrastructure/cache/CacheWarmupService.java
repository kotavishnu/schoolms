package com.school.sms.student.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Cache warmup service for Student Service.
 *
 * Pre-populates cache with frequently accessed data on application startup.
 * Reference: LESSONS_LEARNED.md [D-003]
 *
 * Warmup Strategy:
 * - Load active students into cache (most frequently accessed)
 * - Populate count caches
 * - Execute after application is fully started (ApplicationReadyEvent)
 *
 * Benefits:
 * - Reduces initial request latency
 * - Prevents cache stampede on startup
 * - Improves user experience for first requests
 *
 * @author SMS Backend Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CacheWarmupService implements ApplicationListener<ApplicationReadyEvent> {

    private final CacheManager cacheManager;
    // Note: Repository dependencies would be injected here when available
    // private final StudentRepository studentRepository;

    /**
     * Execute cache warmup after application startup.
     *
     * @param event Application ready event
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Starting cache warmup...");
        long startTime = System.currentTimeMillis();

        try {
            warmupStudentCache();
            warmupCountCache();

            long duration = System.currentTimeMillis() - startTime;
            log.info("Cache warmup completed successfully in {} ms", duration);

        } catch (Exception e) {
            log.error("Cache warmup failed: {}", e.getMessage(), e);
            // Don't fail application startup if warmup fails
        }
    }

    /**
     * Warmup student cache with active students.
     *
     * Loads frequently accessed student records into cache.
     * This prevents database hits for initial requests after startup.
     */
    private void warmupStudentCache() {
        try {
            log.info("Warming up student cache...");

            // TODO: Uncomment when StudentRepository is available
            /*
            var cache = cacheManager.getCache("students");
            if (cache != null) {
                // Load active students (most frequently accessed)
                List<Student> activeStudents = studentRepository.findByStatus(StudentStatus.ACTIVE);

                int cachedCount = 0;
                for (Student student : activeStudents) {
                    String key = "id:" + student.getStudentId().getValue();
                    cache.put(key, student);
                    cachedCount++;

                    // Limit warmup to prevent excessive memory usage
                    if (cachedCount >= 1000) {
                        break;
                    }
                }

                log.info("Cached {} active students", cachedCount);
            }
            */

            log.info("Student cache warmup completed (implementation pending repository availability)");

        } catch (Exception e) {
            log.error("Failed to warmup student cache: {}", e.getMessage(), e);
        }
    }

    /**
     * Warmup count cache.
     *
     * Pre-calculates frequently accessed count queries.
     */
    private void warmupCountCache() {
        try {
            log.info("Warming up count cache...");

            // TODO: Uncomment when StudentRepository is available
            /*
            var cache = cacheManager.getCache("activeStudentsCount");
            if (cache != null) {
                long activeCount = studentRepository.countByStatus(StudentStatus.ACTIVE);
                cache.put("count:active", activeCount);
                log.info("Cached active students count: {}", activeCount);
            }
            */

            log.info("Count cache warmup completed (implementation pending repository availability)");

        } catch (Exception e) {
            log.error("Failed to warmup count cache: {}", e.getMessage(), e);
        }
    }
}
