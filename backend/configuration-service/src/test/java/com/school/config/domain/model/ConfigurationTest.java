package com.school.config.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Configuration domain model.
 * Tests business logic and invariants (TDD RED phase).
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
@DisplayName("Configuration Domain Model Tests")
class ConfigurationTest {

    @Test
    @DisplayName("Should create configuration with valid data")
    void shouldCreateConfigurationWithValidData() {
        // When
        Configuration config = Configuration.create(
            ConfigCategory.GENERAL,
            "school_name",
            "ABC International School",
            "Name of the school",
            DataType.STRING,
            false
        );

        // Then
        assertThat(config).isNotNull();
        assertThat(config.getCategory()).isEqualTo(ConfigCategory.GENERAL);
        assertThat(config.getKey()).isEqualTo("school_name");
        assertThat(config.getValue()).isEqualTo("ABC International School");
        assertThat(config.getDescription()).isEqualTo("Name of the school");
        assertThat(config.getDataType()).isEqualTo(DataType.STRING);
        assertThat(config.isEncrypted()).isFalse();
    }

    @Test
    @DisplayName("Should trim whitespace from key and value")
    void shouldTrimWhitespaceFromKeyAndValue() {
        // When
        Configuration config = Configuration.create(
            ConfigCategory.ACADEMIC,
            "  academic_year  ",
            "  2025-2026  ",
            "Current academic year",
            DataType.STRING,
            false
        );

        // Then
        assertThat(config.getKey()).isEqualTo("academic_year");
        assertThat(config.getValue()).isEqualTo("2025-2026");
    }

    @Test
    @DisplayName("Should throw exception when category is null")
    void shouldThrowExceptionWhenCategoryIsNull() {
        // When / Then
        assertThatThrownBy(() -> Configuration.create(
            null,
            "key",
            "value",
            "description",
            DataType.STRING,
            false
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Category is required");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should throw exception when key is null or blank")
    void shouldThrowExceptionWhenKeyIsNullOrBlank(String key) {
        // When / Then
        assertThatThrownBy(() -> Configuration.create(
            ConfigCategory.GENERAL,
            key,
            "value",
            "description",
            DataType.STRING,
            false
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Configuration key is required");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should throw exception when value is null or blank")
    void shouldThrowExceptionWhenValueIsNullOrBlank(String value) {
        // When / Then
        assertThatThrownBy(() -> Configuration.create(
            ConfigCategory.GENERAL,
            "key",
            value,
            "description",
            DataType.STRING,
            false
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Configuration value is required");
    }

    @Test
    @DisplayName("Should throw exception when data type is null")
    void shouldThrowExceptionWhenDataTypeIsNull() {
        // When / Then
        assertThatThrownBy(() -> Configuration.create(
            ConfigCategory.GENERAL,
            "key",
            "value",
            "description",
            null,
            false
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Data type is required");
    }

    @Test
    @DisplayName("Should allow null description")
    void shouldAllowNullDescription() {
        // When
        Configuration config = Configuration.create(
            ConfigCategory.FINANCIAL,
            "payment_gateway",
            "stripe",
            null,
            DataType.STRING,
            false
        );

        // Then
        assertThat(config.getDescription()).isNull();
    }

    @Test
    @DisplayName("Should update configuration value")
    void shouldUpdateConfigurationValue() {
        // Given
        Configuration config = Configuration.create(
            ConfigCategory.ACADEMIC,
            "class_capacity",
            "30",
            "Maximum students per class",
            DataType.NUMBER,
            false
        );

        // When
        config.updateValue("40");

        // Then
        assertThat(config.getValue()).isEqualTo("40");
    }

    @Test
    @DisplayName("Should trim whitespace when updating value")
    void shouldTrimWhitespaceWhenUpdatingValue() {
        // Given
        Configuration config = Configuration.create(
            ConfigCategory.GENERAL,
            "school_code",
            "SCH001",
            null,
            DataType.STRING,
            false
        );

        // When
        config.updateValue("  SCH002  ");

        // Then
        assertThat(config.getValue()).isEqualTo("SCH002");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    @DisplayName("Should throw exception when updating with null or blank value")
    void shouldThrowExceptionWhenUpdatingWithNullOrBlankValue(String value) {
        // Given
        Configuration config = Configuration.create(
            ConfigCategory.GENERAL,
            "key",
            "original_value",
            null,
            DataType.STRING,
            false
        );

        // When / Then
        assertThatThrownBy(() -> config.updateValue(value))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Configuration value is required");
    }

    @Test
    @DisplayName("Should update description")
    void shouldUpdateDescription() {
        // Given
        Configuration config = Configuration.create(
            ConfigCategory.GENERAL,
            "school_email",
            "admin@school.com",
            "Primary contact email",
            DataType.STRING,
            false
        );

        // When
        config.updateDescription("Updated email address");

        // Then
        assertThat(config.getDescription()).isEqualTo("Updated email address");
    }

    @Test
    @DisplayName("Should update description to null")
    void shouldUpdateDescriptionToNull() {
        // Given
        Configuration config = Configuration.create(
            ConfigCategory.GENERAL,
            "key",
            "value",
            "Old description",
            DataType.STRING,
            false
        );

        // When
        config.updateDescription(null);

        // Then
        assertThat(config.getDescription()).isNull();
    }

    @Test
    @DisplayName("Should validate key format (alphanumeric and underscore only)")
    void shouldValidateKeyFormat() {
        // When / Then - Valid keys
        assertThatCode(() -> Configuration.create(
            ConfigCategory.GENERAL, "valid_key", "value", null, DataType.STRING, false
        )).doesNotThrowAnyException();

        assertThatCode(() -> Configuration.create(
            ConfigCategory.GENERAL, "valid_key_123", "value", null, DataType.STRING, false
        )).doesNotThrowAnyException();

        // When / Then - Invalid keys
        assertThatThrownBy(() -> Configuration.create(
            ConfigCategory.GENERAL, "invalid-key", "value", null, DataType.STRING, false
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Configuration key must contain only letters, numbers, and underscores");

        assertThatThrownBy(() -> Configuration.create(
            ConfigCategory.GENERAL, "invalid.key", "value", null, DataType.STRING, false
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Configuration key must contain only letters, numbers, and underscores");

        assertThatThrownBy(() -> Configuration.create(
            ConfigCategory.GENERAL, "invalid key", "value", null, DataType.STRING, false
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Configuration key must contain only letters, numbers, and underscores");
    }

    @Test
    @DisplayName("Should create configuration with encrypted flag")
    void shouldCreateConfigurationWithEncryptedFlag() {
        // When
        Configuration config = Configuration.create(
            ConfigCategory.FINANCIAL,
            "api_secret_key",
            "sk_test_12345",
            "Stripe API secret",
            DataType.STRING,
            true
        );

        // Then
        assertThat(config.isEncrypted()).isTrue();
    }

    @Test
    @DisplayName("Should get category name as string")
    void shouldGetCategoryNameAsString() {
        // Given
        Configuration config = Configuration.create(
            ConfigCategory.ACADEMIC,
            "key",
            "value",
            null,
            DataType.STRING,
            false
        );

        // Then
        assertThat(config.getCategoryName()).isEqualTo("ACADEMIC");
    }

    @Test
    @DisplayName("Should get data type name as string")
    void shouldGetDataTypeNameAsString() {
        // Given
        Configuration config = Configuration.create(
            ConfigCategory.GENERAL,
            "key",
            "value",
            null,
            DataType.JSON,
            false
        );

        // Then
        assertThat(config.getDataTypeName()).isEqualTo("JSON");
    }
}
