package com.school.configuration.domain.repository;

import com.school.configuration.domain.model.ConfigCategory;
import com.school.configuration.domain.model.Configuration;
import com.school.configuration.domain.model.DataType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ConfigurationRepository Interface Tests")
class ConfigurationRepositoryTest {

    @Test
    @DisplayName("Should define save method")
    void shouldDefineSaveMethod() throws NoSuchMethodException {
        // When
        var method = ConfigurationRepository.class.getMethod("save", Configuration.class);

        // Then
        assertThat(method).isNotNull();
        assertThat(method.getReturnType()).isEqualTo(Configuration.class);
    }

    @Test
    @DisplayName("Should define findById method")
    void shouldDefineFindByIdMethod() throws NoSuchMethodException {
        // When
        var method = ConfigurationRepository.class.getMethod("findById", Long.class);

        // Then
        assertThat(method).isNotNull();
        assertThat(method.getReturnType()).isEqualTo(Optional.class);
    }

    @Test
    @DisplayName("Should define findByCategoryAndKey method")
    void shouldDefineFindByCategoryAndKeyMethod() throws NoSuchMethodException {
        // When
        var method = ConfigurationRepository.class.getMethod("findByCategoryAndKey", ConfigCategory.class, String.class);

        // Then
        assertThat(method).isNotNull();
        assertThat(method.getReturnType()).isEqualTo(Optional.class);
    }

    @Test
    @DisplayName("Should define findByCategory method")
    void shouldDefineFindByCategoryMethod() throws NoSuchMethodException {
        // When
        var method = ConfigurationRepository.class.getMethod("findByCategory", ConfigCategory.class);

        // Then
        assertThat(method).isNotNull();
        assertThat(method.getReturnType()).isEqualTo(List.class);
    }

    @Test
    @DisplayName("Should define findAll method")
    void shouldDefineFindAllMethod() throws NoSuchMethodException {
        // When
        var method = ConfigurationRepository.class.getMethod("findAll");

        // Then
        assertThat(method).isNotNull();
        assertThat(method.getReturnType()).isEqualTo(List.class);
    }

    @Test
    @DisplayName("Should define delete method")
    void shouldDefineDeleteMethod() throws NoSuchMethodException {
        // When
        var method = ConfigurationRepository.class.getMethod("delete", Configuration.class);

        // Then
        assertThat(method).isNotNull();
        assertThat(method.getReturnType()).isEqualTo(void.class);
    }

    @Test
    @DisplayName("Should define existsByCategoryAndKey method")
    void shouldDefineExistsByCategoryAndKeyMethod() throws NoSuchMethodException {
        // When
        var method = ConfigurationRepository.class.getMethod("existsByCategoryAndKey", ConfigCategory.class, String.class);

        // Then
        assertThat(method).isNotNull();
        assertThat(method.getReturnType()).isEqualTo(boolean.class);
    }

    @Test
    @DisplayName("Mock repository should save configuration")
    void mockRepositoryShouldSaveConfiguration() {
        // Given
        ConfigurationRepository repository = mock(ConfigurationRepository.class);
        Configuration configuration = Configuration.builder()
                .category(ConfigCategory.GENERAL)
                .key("SCHOOL_NAME")
                .value("ABC School")
                .dataType(DataType.STRING)
                .build();

        when(repository.save(any(Configuration.class))).thenReturn(configuration);

        // When
        Configuration saved = repository.save(configuration);

        // Then
        assertThat(saved).isNotNull();
        verify(repository, times(1)).save(configuration);
    }

    @Test
    @DisplayName("Mock repository should find by id")
    void mockRepositoryShouldFindById() {
        // Given
        ConfigurationRepository repository = mock(ConfigurationRepository.class);
        Configuration configuration = Configuration.builder()
                .id(1L)
                .category(ConfigCategory.GENERAL)
                .key("SCHOOL_NAME")
                .value("ABC School")
                .dataType(DataType.STRING)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(configuration));

        // When
        Optional<Configuration> found = repository.findById(1L);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(1L);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Mock repository should find by category and key")
    void mockRepositoryShouldFindByCategoryAndKey() {
        // Given
        ConfigurationRepository repository = mock(ConfigurationRepository.class);
        Configuration configuration = Configuration.builder()
                .category(ConfigCategory.GENERAL)
                .key("SCHOOL_NAME")
                .value("ABC School")
                .dataType(DataType.STRING)
                .build();

        when(repository.findByCategoryAndKey(ConfigCategory.GENERAL, "SCHOOL_NAME"))
                .thenReturn(Optional.of(configuration));

        // When
        Optional<Configuration> found = repository.findByCategoryAndKey(ConfigCategory.GENERAL, "SCHOOL_NAME");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getKey()).isEqualTo("SCHOOL_NAME");
        verify(repository, times(1)).findByCategoryAndKey(ConfigCategory.GENERAL, "SCHOOL_NAME");
    }

    @Test
    @DisplayName("Mock repository should find by category")
    void mockRepositoryShouldFindByCategory() {
        // Given
        ConfigurationRepository repository = mock(ConfigurationRepository.class);
        List<Configuration> configurations = Arrays.asList(
                Configuration.builder().category(ConfigCategory.GENERAL).key("KEY1").value("value1").dataType(DataType.STRING).build(),
                Configuration.builder().category(ConfigCategory.GENERAL).key("KEY2").value("value2").dataType(DataType.STRING).build()
        );

        when(repository.findByCategory(ConfigCategory.GENERAL)).thenReturn(configurations);

        // When
        List<Configuration> found = repository.findByCategory(ConfigCategory.GENERAL);

        // Then
        assertThat(found).hasSize(2);
        verify(repository, times(1)).findByCategory(ConfigCategory.GENERAL);
    }

    @Test
    @DisplayName("Mock repository should check existence by category and key")
    void mockRepositoryShouldCheckExistenceByCategoryAndKey() {
        // Given
        ConfigurationRepository repository = mock(ConfigurationRepository.class);
        when(repository.existsByCategoryAndKey(ConfigCategory.GENERAL, "SCHOOL_NAME")).thenReturn(true);

        // When
        boolean exists = repository.existsByCategoryAndKey(ConfigCategory.GENERAL, "SCHOOL_NAME");

        // Then
        assertThat(exists).isTrue();
        verify(repository, times(1)).existsByCategoryAndKey(ConfigCategory.GENERAL, "SCHOOL_NAME");
    }
}
