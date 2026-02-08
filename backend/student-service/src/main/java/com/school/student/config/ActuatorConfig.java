package com.school.student.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Actuator configuration for health checks and metrics.
 * Provides production-ready monitoring endpoints.
 *
 * <p>Available endpoints:
 * <ul>
 *   <li>/actuator/health - Health check with database status</li>
 *   <li>/actuator/metrics - Application metrics</li>
 *   <li>/actuator/prometheus - Prometheus-format metrics</li>
 *   <li>/actuator/info - Application information</li>
 * </ul>
 */
@Configuration
public class ActuatorConfig {

    /**
     * Custom health indicator for application-specific health checks.
     */
    @Component
    @RequiredArgsConstructor
    public static class StudentServiceHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            // Add custom health checks here
            // For now, just return UP
            return Health.up()
                    .withDetail("service", "student-service")
                    .withDetail("version", "1.0.0")
                    .withDetail("status", "operational")
                    .build();
        }
    }
}
