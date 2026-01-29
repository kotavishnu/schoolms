package com.school.configuration.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConfigCategory Enum Tests")
class ConfigCategoryTest {

    @Test
    @DisplayName("Should have GENERAL category")
    void shouldHaveGeneralCategory() {
        // When
        ConfigCategory category = ConfigCategory.GENERAL;

        // Then
        assertThat(category).isNotNull();
        assertThat(category.name()).isEqualTo("GENERAL");
    }

    @Test
    @DisplayName("Should have ACADEMIC category")
    void shouldHaveAcademicCategory() {
        // When
        ConfigCategory category = ConfigCategory.ACADEMIC;

        // Then
        assertThat(category).isNotNull();
        assertThat(category.name()).isEqualTo("ACADEMIC");
    }

    @Test
    @DisplayName("Should have FINANCIAL category")
    void shouldHaveFinancialCategory() {
        // When
        ConfigCategory category = ConfigCategory.FINANCIAL;

        // Then
        assertThat(category).isNotNull();
        assertThat(category.name()).isEqualTo("FINANCIAL");
    }

    @Test
    @DisplayName("Should have exactly 3 categories")
    void shouldHaveExactlyThreeCategories() {
        // When
        ConfigCategory[] categories = ConfigCategory.values();

        // Then
        assertThat(categories).hasSize(3);
    }

    @Test
    @DisplayName("Should parse GENERAL from string")
    void shouldParseGeneralFromString() {
        // When
        ConfigCategory category = ConfigCategory.valueOf("GENERAL");

        // Then
        assertThat(category).isEqualTo(ConfigCategory.GENERAL);
    }
}
