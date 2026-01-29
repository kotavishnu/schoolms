package com.school.configuration.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Configuration Domain Model Tests")
class ConfigurationTest {

    @Test
    @DisplayName("Should create Configuration with all fields")
    void shouldCreateConfigurationWithAllFields() {
        // Given
        Long id = 1L;
        ConfigCategory category = ConfigCategory.GENERAL;
        String key = "SCHOOL_NAME";
        String value = "ABC School";
        String description = "Name of the school";
        DataType dataType = DataType.STRING;
        Boolean isEncrypted = false;
        Long version = 1L;
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
        Configuration configuration = Configuration.builder()
                .id(id)
                .category(category)
                .key(key)
                .value(value)
                .description(description)
                .dataType(dataType)
                .isEncrypted(isEncrypted)
                .version(version)
                .updatedAt(updatedAt)
                .build();

        // Then
        assertThat(configuration).isNotNull();
        assertThat(configuration.getId()).isEqualTo(id);
        assertThat(configuration.getCategory()).isEqualTo(category);
        assertThat(configuration.getKey()).isEqualTo(key);
        assertThat(configuration.getValue()).isEqualTo(value);
        assertThat(configuration.getDescription()).isEqualTo(description);
        assertThat(configuration.getDataType()).isEqualTo(dataType);
        assertThat(configuration.getIsEncrypted()).isEqualTo(isEncrypted);
        assertThat(configuration.getVersion()).isEqualTo(version);
        assertThat(configuration.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("Should create Configuration using no-args constructor")
    void shouldCreateConfigurationUsingNoArgsConstructor() {
        // When
        Configuration configuration = new Configuration();

        // Then
        assertThat(configuration).isNotNull();
    }

    @Test
    @DisplayName("Should create Configuration using all-args constructor")
    void shouldCreateConfigurationUsingAllArgsConstructor() {
        // Given
        Long id = 1L;
        ConfigCategory category = ConfigCategory.ACADEMIC;
        String key = "ACADEMIC_YEAR";
        String value = "2025-2026";
        String description = "Current academic year";
        DataType dataType = DataType.STRING;
        Boolean isEncrypted = false;
        Long version = 1L;
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
        Configuration configuration = new Configuration(
                id, category, key, value, description, dataType, isEncrypted, version, updatedAt
        );

        // Then
        assertThat(configuration).isNotNull();
        assertThat(configuration.getId()).isEqualTo(id);
        assertThat(configuration.getCategory()).isEqualTo(category);
        assertThat(configuration.getKey()).isEqualTo(key);
    }

    @Test
    @DisplayName("Should support GENERAL category")
    void shouldSupportGeneralCategory() {
        // When
        Configuration configuration = Configuration.builder()
                .category(ConfigCategory.GENERAL)
                .key("TEST_KEY")
                .value("test_value")
                .dataType(DataType.STRING)
                .build();

        // Then
        assertThat(configuration.getCategory()).isEqualTo(ConfigCategory.GENERAL);
    }

    @Test
    @DisplayName("Should support ACADEMIC category")
    void shouldSupportAcademicCategory() {
        // When
        Configuration configuration = Configuration.builder()
                .category(ConfigCategory.ACADEMIC)
                .key("TEST_KEY")
                .value("test_value")
                .dataType(DataType.STRING)
                .build();

        // Then
        assertThat(configuration.getCategory()).isEqualTo(ConfigCategory.ACADEMIC);
    }

    @Test
    @DisplayName("Should support FINANCIAL category")
    void shouldSupportFinancialCategory() {
        // When
        Configuration configuration = Configuration.builder()
                .category(ConfigCategory.FINANCIAL)
                .key("TEST_KEY")
                .value("test_value")
                .dataType(DataType.STRING)
                .build();

        // Then
        assertThat(configuration.getCategory()).isEqualTo(ConfigCategory.FINANCIAL);
    }

    @Test
    @DisplayName("Should support STRING data type")
    void shouldSupportStringDataType() {
        // When
        Configuration configuration = Configuration.builder()
                .category(ConfigCategory.GENERAL)
                .key("SCHOOL_NAME")
                .value("ABC School")
                .dataType(DataType.STRING)
                .build();

        // Then
        assertThat(configuration.getDataType()).isEqualTo(DataType.STRING);
    }

    @Test
    @DisplayName("Should support NUMBER data type")
    void shouldSupportNumberDataType() {
        // When
        Configuration configuration = Configuration.builder()
                .category(ConfigCategory.GENERAL)
                .key("MAX_STUDENTS")
                .value("500")
                .dataType(DataType.NUMBER)
                .build();

        // Then
        assertThat(configuration.getDataType()).isEqualTo(DataType.NUMBER);
    }

    @Test
    @DisplayName("Should support BOOLEAN data type")
    void shouldSupportBooleanDataType() {
        // When
        Configuration configuration = Configuration.builder()
                .category(ConfigCategory.GENERAL)
                .key("IS_ACTIVE")
                .value("true")
                .dataType(DataType.BOOLEAN)
                .build();

        // Then
        assertThat(configuration.getDataType()).isEqualTo(DataType.BOOLEAN);
    }

    @Test
    @DisplayName("Should support JSON data type")
    void shouldSupportJsonDataType() {
        // When
        Configuration configuration = Configuration.builder()
                .category(ConfigCategory.GENERAL)
                .key("SETTINGS")
                .value("{\"theme\":\"light\"}")
                .dataType(DataType.JSON)
                .build();

        // Then
        assertThat(configuration.getDataType()).isEqualTo(DataType.JSON);
    }

    @Test
    @DisplayName("Should handle encrypted configurations")
    void shouldHandleEncryptedConfigurations() {
        // When
        Configuration configuration = Configuration.builder()
                .category(ConfigCategory.FINANCIAL)
                .key("API_KEY")
                .value("encrypted_value")
                .dataType(DataType.STRING)
                .isEncrypted(true)
                .build();

        // Then
        assertThat(configuration.getIsEncrypted()).isTrue();
    }

    @Test
    @DisplayName("Should handle non-encrypted configurations")
    void shouldHandleNonEncryptedConfigurations() {
        // When
        Configuration configuration = Configuration.builder()
                .category(ConfigCategory.GENERAL)
                .key("SCHOOL_NAME")
                .value("ABC School")
                .dataType(DataType.STRING)
                .isEncrypted(false)
                .build();

        // Then
        assertThat(configuration.getIsEncrypted()).isFalse();
    }
}
