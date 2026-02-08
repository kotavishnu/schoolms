package com.school.student.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for GuardianInfo value object.
 * Tests Business Rule BR-5: At least one guardian name required.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-03
 */
@DisplayName("GuardianInfo Value Object Tests")
class GuardianInfoTest {

    @Test
    @DisplayName("Should create GuardianInfo with both names")
    void shouldCreateGuardianInfoWithBothNames() {
        // Arrange & Act
        GuardianInfo guardianInfo = GuardianInfo.of("John Doe", "Jane Doe");

        // Assert
        assertThat(guardianInfo).isNotNull();
        assertThat(guardianInfo.getFathersName()).isEqualTo("John Doe");
        assertThat(guardianInfo.getMothersName()).isEqualTo("Jane Doe");
    }

    @Test
    @DisplayName("Should create GuardianInfo with only father's name")
    void shouldCreateGuardianInfoWithOnlyFathersName() {
        // Arrange & Act
        GuardianInfo guardianInfo = GuardianInfo.of("John Doe", null);

        // Assert
        assertThat(guardianInfo).isNotNull();
        assertThat(guardianInfo.getFathersName()).isEqualTo("John Doe");
        assertThat(guardianInfo.getMothersName()).isNull();
    }

    @Test
    @DisplayName("Should create GuardianInfo with only mother's name")
    void shouldCreateGuardianInfoWithOnlyMothersName() {
        // Arrange & Act
        GuardianInfo guardianInfo = GuardianInfo.of(null, "Jane Doe");

        // Assert
        assertThat(guardianInfo).isNotNull();
        assertThat(guardianInfo.getFathersName()).isNull();
        assertThat(guardianInfo.getMothersName()).isEqualTo("Jane Doe");
    }

    @ParameterizedTest
    @CsvSource({
        "'', ''",
        "'  ', '  '",
        "' ', ''",
        "'', ' '"
    })
    @DisplayName("Should throw exception when both names are null or empty (BR-5)")
    void shouldThrowExceptionWhenBothNamesEmpty(String fathersName, String mothersName) {
        // Act & Assert
        assertThatThrownBy(() -> GuardianInfo.of(fathersName, mothersName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("At least one guardian name (father or mother) is required (BR-5)");
    }

    @Test
    @DisplayName("Should throw exception when both names are null")
    void shouldThrowExceptionWhenBothNamesNull() {
        // Act & Assert
        assertThatThrownBy(() -> GuardianInfo.of(null, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("At least one guardian name (father or mother) is required (BR-5)");
    }

    @Test
    @DisplayName("Should trim whitespace from names")
    void shouldTrimWhitespace() {
        // Arrange & Act
        GuardianInfo guardianInfo = GuardianInfo.of("  John Doe  ", "  Jane Doe  ");

        // Assert
        assertThat(guardianInfo.getFathersName()).isEqualTo("John Doe");
        assertThat(guardianInfo.getMothersName()).isEqualTo("Jane Doe");
    }

    @Test
    @DisplayName("Should return father's name as primary guardian when both present")
    void shouldReturnFathersNameAsPrimaryWhenBothPresent() {
        // Arrange
        GuardianInfo guardianInfo = GuardianInfo.of("John Doe", "Jane Doe");

        // Act
        String primaryGuardian = guardianInfo.getPrimaryGuardian();

        // Assert
        assertThat(primaryGuardian).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should return mother's name as primary guardian when father absent")
    void shouldReturnMothersNameAsPrimaryWhenFatherAbsent() {
        // Arrange
        GuardianInfo guardianInfo = GuardianInfo.of(null, "Jane Doe");

        // Act
        String primaryGuardian = guardianInfo.getPrimaryGuardian();

        // Assert
        assertThat(primaryGuardian).isEqualTo("Jane Doe");
    }

    @Test
    @DisplayName("Should be equal when both names are same")
    void shouldBeEqualWhenNamesAreSame() {
        // Arrange
        GuardianInfo info1 = GuardianInfo.of("John Doe", "Jane Doe");
        GuardianInfo info2 = GuardianInfo.of("John Doe", "Jane Doe");

        // Assert
        assertThat(info1).isEqualTo(info2);
        assertThat(info1.hashCode()).isEqualTo(info2.hashCode());
    }
}
