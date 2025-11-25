package com.school.sms.student.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom business metrics for Student Service
 * Tracks student registration, status changes, and API usage
 */
@Slf4j
@Component
public class StudentMetrics {

    private final Counter studentsRegisteredCounter;
    private final Counter studentsUpdatedCounter;
    private final Counter statusChangedCounter;
    private final AtomicInteger activeStudentsCount;
    private final AtomicInteger inactiveStudentsCount;

    public StudentMetrics(MeterRegistry registry) {
        // Counter: Total students registered
        this.studentsRegisteredCounter = Counter.builder("students.registered.total")
                .description("Total number of students registered in the system")
                .tag("service", "student-service")
                .register(registry);

        // Counter: Total students updated
        this.studentsUpdatedCounter = Counter.builder("students.updated.total")
                .description("Total number of student profile updates")
                .tag("service", "student-service")
                .register(registry);

        // Counter: Total status changes
        this.statusChangedCounter = Counter.builder("students.status.changed.total")
                .description("Total number of student status changes")
                .tag("service", "student-service")
                .register(registry);

        // Gauge: Active students count
        this.activeStudentsCount = new AtomicInteger(0);
        Gauge.builder("students.active.count", activeStudentsCount, AtomicInteger::get)
                .description("Current number of active students")
                .tag("service", "student-service")
                .tag("status", "ACTIVE")
                .register(registry);

        // Gauge: Inactive students count
        this.inactiveStudentsCount = new AtomicInteger(0);
        Gauge.builder("students.inactive.count", inactiveStudentsCount, AtomicInteger::get)
                .description("Current number of inactive students")
                .tag("service", "student-service")
                .tag("status", "INACTIVE")
                .register(registry);

        log.info("Student metrics initialized successfully");
    }

    /**
     * Increment registered students counter
     */
    public void incrementRegistered() {
        studentsRegisteredCounter.increment();
        activeStudentsCount.incrementAndGet();
        log.debug("Student registered metric incremented");
    }

    /**
     * Increment updated students counter
     */
    public void incrementUpdated() {
        studentsUpdatedCounter.increment();
        log.debug("Student updated metric incremented");
    }

    /**
     * Increment status changed counter
     */
    public void incrementStatusChanged(String oldStatus, String newStatus) {
        statusChangedCounter.increment();

        // Update active/inactive gauges
        if ("ACTIVE".equals(oldStatus) && !"ACTIVE".equals(newStatus)) {
            activeStudentsCount.decrementAndGet();
            if ("INACTIVE".equals(newStatus)) {
                inactiveStudentsCount.incrementAndGet();
            }
        } else if (!"ACTIVE".equals(oldStatus) && "ACTIVE".equals(newStatus)) {
            activeStudentsCount.incrementAndGet();
            if ("INACTIVE".equals(oldStatus)) {
                inactiveStudentsCount.decrementAndGet();
            }
        }

        log.debug("Student status changed metric incremented: {} -> {}", oldStatus, newStatus);
    }

    /**
     * Set active students count (for initialization)
     */
    public void setActiveStudentsCount(int count) {
        activeStudentsCount.set(count);
    }

    /**
     * Set inactive students count (for initialization)
     */
    public void setInactiveStudentsCount(int count) {
        inactiveStudentsCount.set(count);
    }
}
