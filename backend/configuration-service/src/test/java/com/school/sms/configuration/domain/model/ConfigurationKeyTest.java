package com.school.sms.configuration.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ConfigurationKey Tests")
class ConfigurationKeyTest {

    @Test
    @DisplayName("Create valid ConfigurationKey")
    void of_WithValidKey_ShouldSucceed() {
        ConfigurationKey key = ConfigurationKey.of("school_name");
        assertThat(key.getValue()).isEqualTo("school_name");
    }

    @Test
    @DisplayName("Trim whitespace from key")
    void of_WithWhitespace_ShouldTrim() {
        ConfigurationKey key = ConfigurationKey.of("  school_name  ");
        assertThat(key.getValue()).isEqualTo("school_name");
    }

    @Test
    @DisplayName("Reject null key")
    void of_WithNull_ShouldThrowException() {
        assertThatThrownBy(() -> ConfigurationKey.of(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("Reject empty key")
    void of_WithEmptyString_ShouldThrowException() {
        assertThatThrownBy(() -> ConfigurationKey.of(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("Reject key exceeding max length")
    void of_WithTooLongKey_ShouldThrowException() {
        String longKey = "a".repeat(101);
        assertThatThrownBy(() -> ConfigurationKey.of(longKey))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot exceed 100 characters");
    }

    @Test
    @DisplayName("Equals should work correctly")
    void equals_SameValue_ShouldReturnTrue() {
        ConfigurationKey key1 = ConfigurationKey.of("school_name");
        ConfigurationKey key2 = ConfigurationKey.of("school_name");
        assertThat(key1).isEqualTo(key2);
    }
}
