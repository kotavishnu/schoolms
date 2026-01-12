package com.schoolms.configuration.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Configuration domain model (QA-011 to QA-012)
 * Tests business logic, validation, and domain behavior
 *
 * Coverage:
 * - Valid configuration creation
 * - Category validation
 * - Key format validation
 * - Value updates
 * - Description updates
 * - Required field validation
 */
@DisplayName("Configuration Domain Model Tests")
class ConfigurationTest {

    private static final String VALID_KEY = "SCHOOL_NAME";
    private static final String VALID_VALUE = "Springfield Elementary School";
    private static final String VALID_DESCRIPTION = "Official name of the school";

    @Nested
    @DisplayName("Configuration Creation Tests")
    class ConfigurationCreationTests {

        @Test
        @DisplayName("Should create configuration with valid data successfully")
        void shouldCreateConfigurationWithValidData() {
            // When
            Configuration config = Configuration.create(
                ConfigCategory.GENERAL,
                VALID_KEY,
                VALID_VALUE,
                VALID_DESCRIPTION
            );

            // Then
            assertThat(config).isNotNull();
            assertThat(config.getCategory()).isEqualTo(ConfigCategory.GENERAL);
            assertThat(config.getKey()).isEqualTo(VALID_KEY);
            assertThat(config.getValue()).isEqualTo(VALID_VALUE);
            assertThat(config.getDescription()).isEqualTo(VALID_DESCRIPTION);
            assertThat(config.getCreatedAt()).isNotNull();
            assertThat(config.getLastUpdated()).isNotNull();
            assertThat(config.getVersion()).isEqualTo(0);
        }

        @ParameterizedTest
        @EnumSource(ConfigCategory.class)
        @DisplayName("Should accept all valid categories")
        void shouldAcceptAllValidCategories(ConfigCategory category) {
            // When/Then
            assertThatNoException().isThrownBy(() ->
                Configuration.create(category, VALID_KEY, VALID_VALUE, VALID_DESCRIPTION)
            );
        }

        @Test
        @DisplayName("Should set createdAt and lastUpdated timestamps on creation")
        void shouldSetTimestampsOnCreation() {
            // Given
            Instant beforeCreation = Instant.now().minusSeconds(1);

            // When
            Configuration config = Configuration.create(
                ConfigCategory.ACADEMIC,
                VALID_KEY,
                VALID_VALUE,
                VALID_DESCRIPTION
            );

            // Then
            Instant afterCreation = Instant.now().plusSeconds(1);
            assertThat(config.getCreatedAt()).isBetween(beforeCreation, afterCreation);
            assertThat(config.getLastUpdated()).isBetween(beforeCreation, afterCreation);
        }

        @Test
        @DisplayName("Should accept configuration with minimal description")
        void shouldAcceptConfigurationWithMinimalDescription() {
            // When
            Configuration config = Configuration.create(
                ConfigCategory.SYSTEM,
                VALID_KEY,
                VALID_VALUE,
                "" // Empty description is allowed
            );

            // Then
            assertThat(config).isNotNull();
            assertThat(config.getDescription()).isEmpty();
        }

        @Test
        @DisplayName("Should accept configuration for GENERAL category")
        void shouldAcceptGeneralCategory() {
            // When
            Configuration config = Configuration.create(
                ConfigCategory.GENERAL,
                "SCHOOL_CODE",
                "SPFLD-001",
                "Unique school identifier"
            );

            // Then
            assertThat(config.getCategory()).isEqualTo(ConfigCategory.GENERAL);
        }

        @Test
        @DisplayName("Should accept configuration for ACADEMIC category")
        void shouldAcceptAcademicCategory() {
            // When
            Configuration config = Configuration.create(
                ConfigCategory.ACADEMIC,
                "ACADEMIC_YEAR_START",
                "2026-04-01",
                "Start date of academic year"
            );

            // Then
            assertThat(config.getCategory()).isEqualTo(ConfigCategory.ACADEMIC);
        }

        @Test
        @DisplayName("Should accept configuration for FINANCE category")
        void shouldAcceptFinanceCategory() {
            // When
            Configuration config = Configuration.create(
                ConfigCategory.FINANCE,
                "TUITION_FEE",
                "50000",
                "Annual tuition fee amount"
            );

            // Then
            assertThat(config.getCategory()).isEqualTo(ConfigCategory.FINANCE);
        }

        @Test
        @DisplayName("Should accept configuration for SYSTEM category")
        void shouldAcceptSystemCategory() {
            // When
            Configuration config = Configuration.create(
                ConfigCategory.SYSTEM,
                "MAX_STUDENTS",
                "1000",
                "Maximum number of students allowed"
            );

            // Then
            assertThat(config.getCategory()).isEqualTo(ConfigCategory.SYSTEM);
        }
    }

    @Nested
    @DisplayName("Required Field Validation Tests")
    class RequiredFieldValidationTests {

        @Test
        @DisplayName("Should reject null category")
        void shouldRejectNullCategory() {
            // When/Then
            assertThatThrownBy(() ->
                Configuration.create(null, VALID_KEY, VALID_VALUE, VALID_DESCRIPTION)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Category is required");
        }

        @Test
        @DisplayName("Should reject null key")
        void shouldRejectNullKey() {
            // When/Then
            assertThatThrownBy(() ->
                Configuration.create(ConfigCategory.GENERAL, null, VALID_VALUE, VALID_DESCRIPTION)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Key is required");
        }

        @Test
        @DisplayName("Should reject blank key")
        void shouldRejectBlankKey() {
            // When/Then
            assertThatThrownBy(() ->
                Configuration.create(ConfigCategory.GENERAL, "   ", VALID_VALUE, VALID_DESCRIPTION)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Key is required");
        }

        @Test
        @DisplayName("Should reject empty key")
        void shouldRejectEmptyKey() {
            // When/Then
            assertThatThrownBy(() ->
                Configuration.create(ConfigCategory.GENERAL, "", VALID_VALUE, VALID_DESCRIPTION)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Key is required");
        }

        @Test
        @DisplayName("Should reject null value")
        void shouldRejectNullValue() {
            // When/Then
            assertThatThrownBy(() ->
                Configuration.create(ConfigCategory.GENERAL, VALID_KEY, null, VALID_DESCRIPTION)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Value is required");
        }

        @Test
        @DisplayName("Should reject blank value")
        void shouldRejectBlankValue() {
            // When/Then
            assertThatThrownBy(() ->
                Configuration.create(ConfigCategory.GENERAL, VALID_KEY, "   ", VALID_DESCRIPTION)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Value is required");
        }

        @Test
        @DisplayName("Should reject empty value")
        void shouldRejectEmptyValue() {
            // When/Then
            assertThatThrownBy(() ->
                Configuration.create(ConfigCategory.GENERAL, VALID_KEY, "", VALID_DESCRIPTION)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Value is required");
        }
    }

    @Nested
    @DisplayName("Value Update Tests")
    class ValueUpdateTests {

        @Test
        @DisplayName("Should update value successfully")
        void shouldUpdateValueSuccessfully() {
            // Given
            Configuration config = Configuration.create(
                ConfigCategory.GENERAL,
                VALID_KEY,
                VALID_VALUE,
                VALID_DESCRIPTION
            );
            String newValue = "Updated School Name";

            // When
            config.updateValue(newValue);

            // Then
            assertThat(config.getValue()).isEqualTo(newValue);
        }

        @Test
        @DisplayName("Should update lastUpdated timestamp when value is updated")
        void shouldUpdateTimestampOnValueUpdate() {
            // Given
            Configuration config = Configuration.create(
                ConfigCategory.GENERAL,
                VALID_KEY,
                VALID_VALUE,
                VALID_DESCRIPTION
            );
            Instant originalLastUpdated = config.getLastUpdated();

            // When
            try {
                Thread.sleep(10); // Ensure time difference
            } catch (InterruptedException e) {
                // Ignore
            }
            config.updateValue("New Value");

            // Then
            assertThat(config.getLastUpdated()).isAfter(originalLastUpdated);
        }

        @Test
        @DisplayName("Should not update value when null is provided")
        void shouldNotUpdateValueWhenNull() {
            // Given
            Configuration config = Configuration.create(
                ConfigCategory.GENERAL,
                VALID_KEY,
                VALID_VALUE,
                VALID_DESCRIPTION
            );
            String originalValue = config.getValue();

            // When
            config.updateValue(null);

            // Then
            assertThat(config.getValue()).isEqualTo(originalValue);
        }

        @Test
        @DisplayName("Should not update value when blank is provided")
        void shouldNotUpdateValueWhenBlank() {
            // Given
            Configuration config = Configuration.create(
                ConfigCategory.GENERAL,
                VALID_KEY,
                VALID_VALUE,
                VALID_DESCRIPTION
            );
            String originalValue = config.getValue();

            // When
            config.updateValue("   ");

            // Then
            assertThat(config.getValue()).isEqualTo(originalValue);
        }

        @Test
        @DisplayName("Should not update value when empty is provided")
        void shouldNotUpdateValueWhenEmpty() {
            // Given
            Configuration config = Configuration.create(
                ConfigCategory.GENERAL,
                VALID_KEY,
                VALID_VALUE,
                VALID_DESCRIPTION
            );
            String originalValue = config.getValue();

            // When
            config.updateValue("");

            // Then
            assertThat(config.getValue()).isEqualTo(originalValue);
        }
    }

    @Nested
    @DisplayName("Description Update Tests")
    class DescriptionUpdateTests {

        @Test
        @DisplayName("Should update description successfully")
        void shouldUpdateDescriptionSuccessfully() {
            // Given
            Configuration config = Configuration.create(
                ConfigCategory.GENERAL,
                VALID_KEY,
                VALID_VALUE,
                VALID_DESCRIPTION
            );
            String newDescription = "Updated description";

            // When
            config.updateDescription(newDescription);

            // Then
            assertThat(config.getDescription()).isEqualTo(newDescription);
        }

        @Test
        @DisplayName("Should update lastUpdated timestamp when description is updated")
        void shouldUpdateTimestampOnDescriptionUpdate() {
            // Given
            Configuration config = Configuration.create(
                ConfigCategory.GENERAL,
                VALID_KEY,
                VALID_VALUE,
                VALID_DESCRIPTION
            );
            Instant originalLastUpdated = config.getLastUpdated();

            // When
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // Ignore
            }
            config.updateDescription("New Description");

            // Then
            assertThat(config.getLastUpdated()).isAfter(originalLastUpdated);
        }

        @Test
        @DisplayName("Should allow updating description to empty string")
        void shouldAllowUpdatingDescriptionToEmpty() {
            // Given
            Configuration config = Configuration.create(
                ConfigCategory.GENERAL,
                VALID_KEY,
                VALID_VALUE,
                VALID_DESCRIPTION
            );

            // When
            config.updateDescription("");

            // Then
            assertThat(config.getDescription()).isEmpty();
        }

        @Test
        @DisplayName("Should not update description when null is provided")
        void shouldNotUpdateDescriptionWhenNull() {
            // Given
            Configuration config = Configuration.create(
                ConfigCategory.GENERAL,
                VALID_KEY,
                VALID_VALUE,
                VALID_DESCRIPTION
            );
            String originalDescription = config.getDescription();

            // When
            config.updateDescription(null);

            // Then
            assertThat(config.getDescription()).isEqualTo(originalDescription);
        }
    }

    @Nested
    @DisplayName("Reconstruction Tests")
    class ReconstructionTests {

        @Test
        @DisplayName("Should reconstruct configuration from persistence successfully")
        void shouldReconstructConfigurationFromPersistence() {
            // Given
            Long id = 123L;
            Instant createdAt = Instant.now().minusSeconds(3600);
            Instant lastUpdated = Instant.now();

            // When
            Configuration config = Configuration.reconstruct(
                id,
                ConfigCategory.ACADEMIC,
                VALID_KEY,
                VALID_VALUE,
                VALID_DESCRIPTION,
                createdAt,
                lastUpdated,
                5
            );

            // Then
            assertThat(config).isNotNull();
            assertThat(config.getId()).isEqualTo(id);
            assertThat(config.getCategory()).isEqualTo(ConfigCategory.ACADEMIC);
            assertThat(config.getKey()).isEqualTo(VALID_KEY);
            assertThat(config.getValue()).isEqualTo(VALID_VALUE);
            assertThat(config.getDescription()).isEqualTo(VALID_DESCRIPTION);
            assertThat(config.getCreatedAt()).isEqualTo(createdAt);
            assertThat(config.getLastUpdated()).isEqualTo(lastUpdated);
            assertThat(config.getVersion()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("Key Format Tests")
    class KeyFormatTests {

        @ParameterizedTest
        @ValueSource(strings = {
            "SCHOOL_NAME",
            "ACADEMIC_YEAR_START",
            "MAX_STUDENTS_PER_CLASS",
            "TUITION_FEE_2026",
            "SYSTEM_VERSION"
        })
        @DisplayName("Should accept various valid key formats")
        void shouldAcceptVariousValidKeyFormats(String key) {
            // When/Then
            assertThatNoException().isThrownBy(() ->
                Configuration.create(ConfigCategory.GENERAL, key, VALID_VALUE, VALID_DESCRIPTION)
            );
        }
    }
}
