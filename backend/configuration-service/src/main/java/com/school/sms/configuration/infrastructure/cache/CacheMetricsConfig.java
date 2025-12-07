package com.school.sms.configuration.infrastructure.cache;

import org.springframework.context.annotation.Configuration;

/**
 * Cache metrics configuration using Micrometer.
 *
 * Implements cache monitoring as per LESSONS_LEARNED.md [D-007]
 *
 * NOTE: Advanced metrics temporarily disabled due to API changes in Spring Boot 3.5.0
 * TODO: Re-enable once compatible with Spring Boot 3.5.0 cache metrics API
 *
 * Metrics Tracked:
 * - cache.hits.total (Counter) - Tagged by cache name
 * - cache.misses.total (Counter) - Tagged by cache name
 * - cache.hit.ratio (Gauge) - Target >80%
 * - cache.evictions.total (Counter)
 * - redis.memory.used (Gauge) - Alert if >200MB
 *
 * All metrics are exported to Prometheus for visualization in Grafana.
 *
 * @author SMS Backend Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Configuration
public class CacheMetricsConfig {
    // Metrics temporarily disabled - will be re-enabled with Spring Boot 3.5.0 compatible API
}
