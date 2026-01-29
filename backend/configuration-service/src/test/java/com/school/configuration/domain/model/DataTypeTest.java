package com.school.configuration.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DataType Enum Tests")
class DataTypeTest {

    @Test
    @DisplayName("Should have STRING data type")
    void shouldHaveStringDataType() {
        // When
        DataType dataType = DataType.STRING;

        // Then
        assertThat(dataType).isNotNull();
        assertThat(dataType.name()).isEqualTo("STRING");
    }

    @Test
    @DisplayName("Should have NUMBER data type")
    void shouldHaveNumberDataType() {
        // When
        DataType dataType = DataType.NUMBER;

        // Then
        assertThat(dataType).isNotNull();
        assertThat(dataType.name()).isEqualTo("NUMBER");
    }

    @Test
    @DisplayName("Should have BOOLEAN data type")
    void shouldHaveBooleanDataType() {
        // When
        DataType dataType = DataType.BOOLEAN;

        // Then
        assertThat(dataType).isNotNull();
        assertThat(dataType.name()).isEqualTo("BOOLEAN");
    }

    @Test
    @DisplayName("Should have JSON data type")
    void shouldHaveJsonDataType() {
        // When
        DataType dataType = DataType.JSON;

        // Then
        assertThat(dataType).isNotNull();
        assertThat(dataType.name()).isEqualTo("JSON");
    }

    @Test
    @DisplayName("Should have exactly 4 data types")
    void shouldHaveExactlyFourDataTypes() {
        // When
        DataType[] dataTypes = DataType.values();

        // Then
        assertThat(dataTypes).hasSize(4);
    }

    @Test
    @DisplayName("Should parse STRING from string")
    void shouldParseStringFromString() {
        // When
        DataType dataType = DataType.valueOf("STRING");

        // Then
        assertThat(dataType).isEqualTo(DataType.STRING);
    }
}
