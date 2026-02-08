package com.school.student.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Custom metrics for student operations.
 * Integrates with Micrometer for Prometheus monitoring.
 *
 * <p>Metrics:
 * <ul>
 *   <li>students.registered.total - Counter for successful registrations</li>
 *   <li>students.updated.total - Counter for successful updates</li>
 *   <li>students.deleted.total - Counter for deletions</li>
 *   <li>students.validation.failed.total - Counter for validation failures</li>
 * </ul>
 */
@Component
@Slf4j
public class StudentMetrics {

    private final Counter registrationCounter;
    private final Counter updateCounter;
    private final Counter deleteCounter;
    private final Counter validationFailureCounter;

    public StudentMetrics(MeterRegistry meterRegistry) {
        // Create counters
        this.registrationCounter = Counter.builder("students.registered.total")
                .description("Total number of students successfully registered")
                .tag("service", "student-service")
                .register(meterRegistry);

        this.updateCounter = Counter.builder("students.updated.total")
                .description("Total number of students successfully updated")
                .tag("service", "student-service")
                .register(meterRegistry);

        this.deleteCounter = Counter.builder("students.deleted.total")
                .description("Total number of students deleted")
                .tag("service", "student-service")
                .register(meterRegistry);

        this.validationFailureCounter = Counter.builder("students.validation.failed.total")
                .description("Total number of validation failures")
                .tag("service", "student-service")
                .register(meterRegistry);

        log.info("Student metrics initialized");
    }

    /**
     * Increment registration counter.
     */
    public void incrementRegistrations() {
        registrationCounter.increment();
        log.debug("Registration counter incremented: {}", registrationCounter.count());
    }

    /**
     * Increment update counter.
     */
    public void incrementUpdates() {
        updateCounter.increment();
        log.debug("Update counter incremented: {}", updateCounter.count());
    }

    /**
     * Increment delete counter.
     */
    public void incrementDeletes() {
        deleteCounter.increment();
        log.debug("Delete counter incremented: {}", deleteCounter.count());
    }

    /**
     * Increment validation failure counter.
     *
     * @param errorCode the validation error code
     */
    public void incrementValidationFailures(String errorCode) {
        validationFailureCounter.increment();
        log.debug("Validation failure counter incremented for code: {}", errorCode);
    }
}
