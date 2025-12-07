package com.school.sms.student.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * Redis health indicator for Student Service.
 *
 * Monitors Redis connectivity and reports health status to Spring Boot Actuator.
 * Reference: LESSONS_LEARNED.md [D-007]
 *
 * Health Status:
 * - UP: Redis is available and responding to ping
 * - DOWN: Redis is unavailable or not responding
 *
 * Details Included:
 * - Redis database number
 * - Connection status
 * - Error message (if down)
 *
 * @author SMS Backend Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * Check Redis health by attempting to ping the server.
     *
     * @return Health status with details
     */
    @Override
    public Health health() {
        try {
            // Attempt to ping Redis
            var connection = redisConnectionFactory.getConnection();
            String pingResponse = connection.ping();

            // Close connection
            connection.close();

            log.debug("Redis health check successful - Ping response: {}", pingResponse);

            return Health.up()
                    .withDetail("redis", "Available")
                    .withDetail("service", "student-service")
                    .withDetail("ping", pingResponse)
                    .build();

        } catch (Exception e) {
            log.error("Redis health check failed: {}", e.getMessage(), e);

            return Health.down()
                    .withDetail("redis", "Unavailable")
                    .withDetail("error", e.getMessage())
                    .withDetail("service", "student-service")
                    .build();
        }
    }
}
