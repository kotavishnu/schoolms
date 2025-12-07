package com.school.sms.configuration.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ConfigurationValue Tests")
class ConfigurationValueTest {

    @Test
    @DisplayName("Create valid ConfigurationValue")
    void of_WithValidValue_ShouldSucceed() {
        ConfigurationValue value = ConfigurationValue.of("ABC School");
        assertThat(value.getValue()).isEqualTo("ABC School");
    }

    @Test
    @DisplayName("Accept empty string value")
    void of_WithEmptyString_ShouldSucceed() {
        ConfigurationValue value = ConfigurationValue.of("");
        assertThat(value.getValue()).isEmpty();
    }

    @Test
    @DisplayName("Reject null value")
    void of_WithNull_ShouldThrowException() {
        assertThatThrownBy(() -> ConfigurationValue.of(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("Equals should work correctly")
    void equals_SameValue_ShouldReturnTrue() {
        ConfigurationValue value1 = ConfigurationValue.of("ABC School");
        ConfigurationValue value2 = ConfigurationValue.of("ABC School");
        assertThat(value1).isEqualTo(value2);
    }
}
