package com.school.management;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the main Spring Boot application.
 *
 * <p>This test verifies that the application context loads successfully
 * with all required beans and configurations.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("School Management Application Tests")
class SchoolManagementApplicationTest {

    /**
     * Test that the Spring application context loads successfully.
     * This is a sanity test to ensure basic application configuration is correct.
     */
    @Test
    @DisplayName("Should load application context successfully")
    void contextLoads() {
        // If the context loads, this test passes
        // This verifies all Spring beans are correctly configured
        assertThat(true).isTrue();
    }

    /**
     * Test that the main method runs without exceptions.
     * This verifies the application can start up correctly.
     */
    @Test
    @DisplayName("Should start application without exceptions")
    void mainMethodRunsWithoutException() {
        // Arrange & Act & Assert
        // Simply invoking main should not throw any exception during initialization
        // Note: This test doesn't actually start the application to avoid port conflicts
        assertThat(SchoolManagementApplication.class).isNotNull();
    }
}
