package com.school.configuration.domain.exception;

import com.school.configuration.domain.model.ConfigCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConfigurationNotFoundException Tests")
class ConfigurationNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with id")
    void shouldCreateExceptionWithId() {
        // Given
        Long id = 1L;

        // When
        ConfigurationNotFoundException exception = new ConfigurationNotFoundException(id);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains("Configuration not found with id: 1");
    }

    @Test
    @DisplayName("Should create exception with category and key")
    void shouldCreateExceptionWithCategoryAndKey() {
        // Given
        ConfigCategory category = ConfigCategory.GENERAL;
        String key = "SCHOOL_NAME";

        // When
        ConfigurationNotFoundException exception = new ConfigurationNotFoundException(category, key);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains("Configuration not found with category: GENERAL and key: SCHOOL_NAME");
    }

    @Test
    @DisplayName("Should extend BusinessException")
    void shouldExtendBusinessException() {
        // When
        ConfigurationNotFoundException exception = new ConfigurationNotFoundException(1L);

        // Then
        assertThat(exception).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeInstanceOfRuntimeException() {
        // When
        ConfigurationNotFoundException exception = new ConfigurationNotFoundException(1L);

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
