package com.school.management.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class to verify application properties are loaded correctly.
 *
 * <p>This test ensures that configuration from application-test.yml
 * is properly loaded and accessible via @Value injection.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Application Properties Tests")
class ApplicationPropertiesTest {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${application.student.code-prefix}")
    private String studentCodePrefix;

    @Value("${application.student.min-age}")
    private int minAge;

    @Value("${application.student.max-age}")
    private int maxAge;

    @Value("${application.fee.receipt-prefix}")
    private String receiptPrefix;

    @Value("${application.pagination.default-page-size}")
    private int defaultPageSize;

    @Value("${application.pagination.max-page-size}")
    private int maxPageSize;

    @Test
    @DisplayName("Should load application name from properties")
    void shouldLoadApplicationName() {
        // Assert
        assertThat(applicationName)
                .isNotNull()
                .isEqualTo("school-management-system-test");
    }

    @Test
    @DisplayName("Should load student configuration properties")
    void shouldLoadStudentConfiguration() {
        // Assert
        assertThat(studentCodePrefix)
                .isNotNull()
                .isEqualTo("TST");

        assertThat(minAge)
                .isEqualTo(3);

        assertThat(maxAge)
                .isEqualTo(18);
    }

    @Test
    @DisplayName("Should load fee configuration properties")
    void shouldLoadFeeConfiguration() {
        // Assert
        assertThat(receiptPrefix)
                .isNotNull()
                .isEqualTo("REC");
    }

    @Test
    @DisplayName("Should load pagination configuration properties")
    void shouldLoadPaginationConfiguration() {
        // Assert
        assertThat(defaultPageSize)
                .isEqualTo(10);

        assertThat(maxPageSize)
                .isEqualTo(50);
    }

    @Test
    @DisplayName("Should validate business rule constraints from configuration")
    void shouldValidateBusinessRuleConstraints() {
        // Assert - Verify BR-1: Student age 3-18 years
        assertThat(minAge)
                .isGreaterThanOrEqualTo(3)
                .isLessThanOrEqualTo(maxAge);

        assertThat(maxAge)
                .isGreaterThanOrEqualTo(minAge)
                .isLessThanOrEqualTo(18);
    }
}
