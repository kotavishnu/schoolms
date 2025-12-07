package com.school.sms.configuration.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Cache warmup service for Configuration Service.
 *
 * Pre-populates cache with all configuration settings on application startup.
 * Reference: LESSONS_LEARNED.md [D-003]
 *
 * Warmup Strategy:
 * - Load all configuration settings (relatively small dataset)
 * - Group by category for faster lookups
 * - Execute after application is fully started (ApplicationReadyEvent)
 *
 * Benefits:
 * - All configuration requests served from cache immediately
 * - No database hits for configuration lookups
 * - Consistent performance from first request
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
    // private final ConfigurationRepository configurationRepository;

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
            warmupConfigurationCache();

            long duration = System.currentTimeMillis() - startTime;
            log.info("Cache warmup completed successfully in {} ms", duration);

        } catch (Exception e) {
            log.error("Cache warmup failed: {}", e.getMessage(), e);
            // Don't fail application startup if warmup fails
        }
    }

    /**
     * Warmup configuration cache with all settings.
     *
     * Loads all configuration settings into cache.
     * Configuration data is small and stable, so we can cache it all.
     */
    private void warmupConfigurationCache() {
        try {
            log.info("Warming up configuration cache...");

            // TODO: Uncomment when ConfigurationRepository is available
            /*
            var configurationsCache = cacheManager.getCache("configurations");
            var groupedCache = cacheManager.getCache("groupedConfigs");

            if (configurationsCache != null) {
                // Load all configurations
                List<ConfigurationSetting> allConfigs = configurationRepository.findAll();

                int cachedCount = 0;
                Map<String, Map<String, String>> groupedByCategory = new HashMap<>();

                for (ConfigurationSetting config : allConfigs) {
                    // Cache individual configuration
                    String key = "category:" + config.getCategory() + ":key:" + config.getKey();
                    configurationsCache.put(key, config);
                    cachedCount++;

                    // Build grouped map
                    String category = config.getCategory().getValue();
                    groupedByCategory.computeIfAbsent(category, k -> new HashMap<>())
                            .put(config.getKey().getValue(), config.getValue().getValue());
                }

                // Cache grouped configurations
                if (groupedCache != null) {
                    for (Map.Entry<String, Map<String, String>> entry : groupedByCategory.entrySet()) {
                        String groupedKey = "grouped:" + entry.getKey();
                        groupedCache.put(groupedKey, entry.getValue());
                    }
                }

                log.info("Cached {} configurations across {} categories",
                        cachedCount, groupedByCategory.size());
            }
            */

            log.info("Configuration cache warmup completed (implementation pending repository availability)");

        } catch (Exception e) {
            log.error("Failed to warmup configuration cache: {}", e.getMessage(), e);
        }
    }
}
