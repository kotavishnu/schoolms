package com.school.student.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for StudentMetrics.
 * Tests counter increments with MeterRegistry.
 *
 * <p>Following D-013: Infrastructure Layer Testing - Test metrics with mock registry.
 */
@DisplayName("StudentMetrics Unit Tests")
class StudentMetricsTest {

    private MeterRegistry meterRegistry;
    private StudentMetrics studentMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        studentMetrics = new StudentMetrics(meterRegistry);
    }

    // ==================== Initialization Tests ====================

    @Test
    @DisplayName("Should initialize with MeterRegistry")
    void shouldInitializeWithMeterRegistry() {
        // Assert
        assertThat(studentMetrics).isNotNull();
    }

    @Test
    @DisplayName("Should register all counters on initialization")
    void shouldRegisterAllCountersOnInitialization() {
        // Act - Create metrics instance
        new StudentMetrics(meterRegistry);

        // Assert
        assertThat(meterRegistry.find("students.registered.total").counter()).isNotNull();
        assertThat(meterRegistry.find("students.updated.total").counter()).isNotNull();
        assertThat(meterRegistry.find("students.deleted.total").counter()).isNotNull();
        assertThat(meterRegistry.find("students.validation.failed.total").counter()).isNotNull();
    }

    // ==================== Registration Counter Tests ====================

    @Test
    @DisplayName("Should increment registration counter")
    void shouldIncrementRegistrationCounter() {
        // Act
        studentMetrics.incrementRegistrations();

        // Assert
        Counter counter = meterRegistry.find("students.registered.total").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should increment registration counter multiple times")
    void shouldIncrementRegistrationCounterMultipleTimes() {
        // Act
        studentMetrics.incrementRegistrations();
        studentMetrics.incrementRegistrations();
        studentMetrics.incrementRegistrations();

        // Assert
        Counter counter = meterRegistry.find("students.registered.total").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(3.0);
    }

    // ==================== Update Counter Tests ====================

    @Test
    @DisplayName("Should increment update counter")
    void shouldIncrementUpdateCounter() {
        // Act
        studentMetrics.incrementUpdates();

        // Assert
        Counter counter = meterRegistry.find("students.updated.total").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should increment update counter multiple times")
    void shouldIncrementUpdateCounterMultipleTimes() {
        // Act
        studentMetrics.incrementUpdates();
        studentMetrics.incrementUpdates();

        // Assert
        Counter counter = meterRegistry.find("students.updated.total").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);
    }

    // ==================== Delete Counter Tests ====================

    @Test
    @DisplayName("Should increment delete counter")
    void shouldIncrementDeleteCounter() {
        // Act
        studentMetrics.incrementDeletes();

        // Assert
        Counter counter = meterRegistry.find("students.deleted.total").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should increment delete counter multiple times")
    void shouldIncrementDeleteCounterMultipleTimes() {
        // Act
        for (int i = 0; i < 5; i++) {
            studentMetrics.incrementDeletes();
        }

        // Assert
        Counter counter = meterRegistry.find("students.deleted.total").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(5.0);
    }

    // ==================== Validation Failure Counter Tests ====================

    @Test
    @DisplayName("Should increment validation failure counter")
    void shouldIncrementValidationFailureCounter() {
        // Act
        studentMetrics.incrementValidationFailures("AGE_OUT_OF_RANGE");

        // Assert
        Counter counter = meterRegistry.find("students.validation.failed.total").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should increment validation failure counter with different error codes")
    void shouldIncrementValidationFailureCounterWithDifferentErrorCodes() {
        // Act
        studentMetrics.incrementValidationFailures("AGE_OUT_OF_RANGE");
        studentMetrics.incrementValidationFailures("MOBILE_DUPLICATE");
        studentMetrics.incrementValidationFailures("REQUIRED_FIELD");

        // Assert
        Counter counter = meterRegistry.find("students.validation.failed.total").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(3.0);
    }

    // ==================== Counter Tags Tests ====================

    @Test
    @DisplayName("Should have service tag on registration counter")
    void shouldHaveServiceTagOnRegistrationCounter() {
        // Act
        studentMetrics.incrementRegistrations();

        // Assert
        Counter counter = meterRegistry.find("students.registered.total")
            .tag("service", "student-service")
            .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should have service tag on all counters")
    void shouldHaveServiceTagOnAllCounters() {
        // Act
        studentMetrics.incrementRegistrations();
        studentMetrics.incrementUpdates();
        studentMetrics.incrementDeletes();
        studentMetrics.incrementValidationFailures("CODE");

        // Assert
        assertThat(meterRegistry.find("students.registered.total").tag("service", "student-service").counter())
            .isNotNull();
        assertThat(meterRegistry.find("students.updated.total").tag("service", "student-service").counter())
            .isNotNull();
        assertThat(meterRegistry.find("students.deleted.total").tag("service", "student-service").counter())
            .isNotNull();
        assertThat(meterRegistry.find("students.validation.failed.total").tag("service", "student-service").counter())
            .isNotNull();
    }

    // ==================== Mixed Operations Tests ====================

    @Test
    @DisplayName("Should handle mixed metric operations")
    void shouldHandleMixedMetricOperations() {
        // Act
        studentMetrics.incrementRegistrations();
        studentMetrics.incrementRegistrations();
        studentMetrics.incrementUpdates();
        studentMetrics.incrementDeletes();
        studentMetrics.incrementValidationFailures("ERROR");

        // Assert
        assertThat(meterRegistry.find("students.registered.total").counter().count()).isEqualTo(2.0);
        assertThat(meterRegistry.find("students.updated.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.find("students.deleted.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.find("students.validation.failed.total").counter().count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should maintain independent counter states")
    void shouldMaintainIndependentCounterStates() {
        // Act
        studentMetrics.incrementRegistrations();
        studentMetrics.incrementRegistrations();
        studentMetrics.incrementRegistrations();
        studentMetrics.incrementUpdates();

        // Assert
        Counter regCounter = meterRegistry.find("students.registered.total").counter();
        Counter updateCounter = meterRegistry.find("students.updated.total").counter();
        Counter deleteCounter = meterRegistry.find("students.deleted.total").counter();

        assertThat(regCounter.count()).isEqualTo(3.0);
        assertThat(updateCounter.count()).isEqualTo(1.0);
        assertThat(deleteCounter.count()).isEqualTo(0.0);
    }

    // ==================== Edge Cases Tests ====================

    @Test
    @DisplayName("Should handle null error code in validation failure")
    void shouldHandleNullErrorCodeInValidationFailure() {
        // Act & Assert - Should not throw exception
        assertThatCode(() -> studentMetrics.incrementValidationFailures(null))
            .doesNotThrowAnyException();

        Counter counter = meterRegistry.find("students.validation.failed.total").counter();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should handle high volume of increments")
    void shouldHandleHighVolumeOfIncrements() {
        // Act
        for (int i = 0; i < 1000; i++) {
            studentMetrics.incrementRegistrations();
        }

        // Assert
        Counter counter = meterRegistry.find("students.registered.total").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1000.0);
    }
}
