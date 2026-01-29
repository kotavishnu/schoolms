package com.school.configuration.infrastructure.persistence;

import com.school.configuration.domain.model.ConfigCategory;
import com.school.configuration.domain.model.Configuration;
import com.school.configuration.domain.model.DataType;
import com.school.configuration.domain.repository.ConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for Configuration Repository using TestContainers.
 * Tests CRUD operations, enum conversions, and database constraints.
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.school.configuration.infrastructure.persistence")
@DisplayName("Configuration Repository Integration Tests")
class ConfigurationRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
        .withDatabaseName("config_db_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private ConfigurationJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        jpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save configuration successfully")
    void shouldSaveConfigurationSuccessfully() {
        // Given
        Configuration configuration = Configuration.builder()
            .category(ConfigCategory.GENERAL)
            .key("SCHOOL_NAME")
            .value("Test School")
            .description("Name of the school")
            .dataType(DataType.STRING)
            .isEncrypted(false)
            .build();

        // When
        Configuration saved = configurationRepository.save(configuration);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCategory()).isEqualTo(ConfigCategory.GENERAL);
        assertThat(saved.getKey()).isEqualTo("SCHOOL_NAME");
        assertThat(saved.getValue()).isEqualTo("Test School");
        assertThat(saved.getDataType()).isEqualTo(DataType.STRING);
        assertThat(saved.getVersion()).isEqualTo(0L);
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find configuration by ID")
    void shouldFindConfigurationById() {
        // Given
        Configuration configuration = createAndSaveConfiguration(
            ConfigCategory.ACADEMIC,
            "ACADEMIC_YEAR",
            "2024-2025"
        );

        // When
        Optional<Configuration> found = configurationRepository.findById(configuration.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getKey()).isEqualTo("ACADEMIC_YEAR");
        assertThat(found.get().getValue()).isEqualTo("2024-2025");
    }

    @Test
    @DisplayName("Should find configuration by category and key")
    void shouldFindConfigurationByCategoryAndKey() {
        // Given
        createAndSaveConfiguration(ConfigCategory.GENERAL, "SCHOOL_NAME", "Test School");

        // When
        Optional<Configuration> found = configurationRepository.findByCategoryAndKey(
            ConfigCategory.GENERAL,
            "SCHOOL_NAME"
        );

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getValue()).isEqualTo("Test School");
    }

    @Test
    @DisplayName("Should find all configurations by category")
    void shouldFindAllConfigurationsByCategory() {
        // Given
        createAndSaveConfiguration(ConfigCategory.GENERAL, "SCHOOL_NAME", "Test School");
        createAndSaveConfiguration(ConfigCategory.GENERAL, "SCHOOL_ADDRESS", "123 Main St");
        createAndSaveConfiguration(ConfigCategory.ACADEMIC, "ACADEMIC_YEAR", "2024-2025");

        // When
        List<Configuration> generalConfigs = configurationRepository.findByCategory(ConfigCategory.GENERAL);

        // Then
        assertThat(generalConfigs).hasSize(2);
        assertThat(generalConfigs)
            .extracting(Configuration::getKey)
            .containsExactlyInAnyOrder("SCHOOL_NAME", "SCHOOL_ADDRESS");
    }

    @Test
    @DisplayName("Should find all configurations")
    void shouldFindAllConfigurations() {
        // Given
        createAndSaveConfiguration(ConfigCategory.GENERAL, "KEY1", "Value1");
        createAndSaveConfiguration(ConfigCategory.ACADEMIC, "KEY2", "Value2");
        createAndSaveConfiguration(ConfigCategory.FINANCIAL, "KEY3", "Value3");

        // When
        List<Configuration> allConfigs = configurationRepository.findAll();

        // Then
        assertThat(allConfigs).hasSize(3);
    }

    @Test
    @DisplayName("Should check if configuration exists by category and key")
    void shouldCheckIfConfigurationExists() {
        // Given
        createAndSaveConfiguration(ConfigCategory.GENERAL, "SCHOOL_NAME", "Test School");

        // When
        boolean exists = configurationRepository.existsByCategoryAndKey(
            ConfigCategory.GENERAL,
            "SCHOOL_NAME"
        );
        boolean notExists = configurationRepository.existsByCategoryAndKey(
            ConfigCategory.GENERAL,
            "NON_EXISTENT_KEY"
        );

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should delete configuration")
    void shouldDeleteConfiguration() {
        // Given
        Configuration configuration = createAndSaveConfiguration(
            ConfigCategory.GENERAL,
            "TEMP_KEY",
            "Temp Value"
        );
        Long configId = configuration.getId();

        // When
        configurationRepository.delete(configuration);

        // Then
        Optional<Configuration> found = configurationRepository.findById(configId);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should enforce composite unique constraint on category and key")
    void shouldEnforceCompositeUniqueConstraint() {
        // Given
        createAndSaveConfiguration(ConfigCategory.GENERAL, "SCHOOL_NAME", "Test School");

        // When/Then - Attempting to save duplicate category+key should throw exception
        assertThatThrownBy(() -> {
            ConfigurationEntity duplicate = ConfigurationEntity.builder()
                .category("GENERAL")
                .key("SCHOOL_NAME")
                .value("Duplicate School")
                .dataType("STRING")
                .isEncrypted(false)
                .version(0L)
                .updatedAt(LocalDateTime.now())
                .build();
            jpaRepository.saveAndFlush(duplicate);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should properly convert enum types to and from database")
    void shouldProperlyConvertEnumTypes() {
        // Given
        Configuration configuration = Configuration.builder()
            .category(ConfigCategory.FINANCIAL)
            .key("PAYMENT_GATEWAY")
            .value("Stripe")
            .description("Payment gateway provider")
            .dataType(DataType.JSON)
            .isEncrypted(true)
            .build();

        // When
        Configuration saved = configurationRepository.save(configuration);
        Configuration retrieved = configurationRepository.findById(saved.getId()).orElseThrow();

        // Then - Enums should be properly converted to/from String in database
        assertThat(retrieved.getCategory()).isEqualTo(ConfigCategory.FINANCIAL);
        assertThat(retrieved.getDataType()).isEqualTo(DataType.JSON);
    }

    @Test
    @DisplayName("Should update configuration with optimistic locking")
    void shouldUpdateConfigurationWithOptimisticLocking() {
        // Given
        Configuration original = createAndSaveConfiguration(
            ConfigCategory.GENERAL,
            "SCHOOL_NAME",
            "Original Name"
        );
        Long originalVersion = original.getVersion();

        // When - Update the configuration
        Configuration updated = Configuration.builder()
            .id(original.getId())
            .category(original.getCategory())
            .key(original.getKey())
            .value("Updated Name")
            .description(original.getDescription())
            .dataType(original.getDataType())
            .isEncrypted(original.getIsEncrypted())
            .version(originalVersion)
            .build();

        Configuration saved = configurationRepository.save(updated);

        // Then - Version should be incremented, value should be updated
        assertThat(saved.getValue()).isEqualTo("Updated Name");
        assertThat(saved.getVersion()).isNotNull();

        // Re-fetch from database to verify persistence and version increment
        Configuration refetched = configurationRepository.findById(saved.getId()).orElseThrow();
        assertThat(refetched.getValue()).isEqualTo("Updated Name");
        assertThat(refetched.getVersion()).isEqualTo(saved.getVersion());
    }

    @Test
    @DisplayName("Should handle all configuration categories")
    void shouldHandleAllConfigurationCategories() {
        // Given/When
        Configuration general = createAndSaveConfiguration(ConfigCategory.GENERAL, "KEY1", "Value1");
        Configuration academic = createAndSaveConfiguration(ConfigCategory.ACADEMIC, "KEY2", "Value2");
        Configuration financial = createAndSaveConfiguration(ConfigCategory.FINANCIAL, "KEY3", "Value3");

        // Then
        assertThat(general.getCategory()).isEqualTo(ConfigCategory.GENERAL);
        assertThat(academic.getCategory()).isEqualTo(ConfigCategory.ACADEMIC);
        assertThat(financial.getCategory()).isEqualTo(ConfigCategory.FINANCIAL);
    }

    @Test
    @DisplayName("Should handle all data types")
    void shouldHandleAllDataTypes() {
        // Given/When
        Configuration stringType = Configuration.builder()
            .category(ConfigCategory.GENERAL)
            .key("STRING_KEY")
            .value("text")
            .dataType(DataType.STRING)
            .isEncrypted(false)
            .build();

        Configuration numberType = Configuration.builder()
            .category(ConfigCategory.GENERAL)
            .key("NUMBER_KEY")
            .value("100")
            .dataType(DataType.NUMBER)
            .isEncrypted(false)
            .build();

        Configuration booleanType = Configuration.builder()
            .category(ConfigCategory.GENERAL)
            .key("BOOLEAN_KEY")
            .value("true")
            .dataType(DataType.BOOLEAN)
            .isEncrypted(false)
            .build();

        Configuration jsonType = Configuration.builder()
            .category(ConfigCategory.GENERAL)
            .key("JSON_KEY")
            .value("{\"key\":\"value\"}")
            .dataType(DataType.JSON)
            .isEncrypted(false)
            .build();

        configurationRepository.save(stringType);
        configurationRepository.save(numberType);
        configurationRepository.save(booleanType);
        configurationRepository.save(jsonType);

        // Then
        List<Configuration> allConfigs = configurationRepository.findAll();
        assertThat(allConfigs).hasSize(4);
        assertThat(allConfigs)
            .extracting(Configuration::getDataType)
            .containsExactlyInAnyOrder(DataType.STRING, DataType.NUMBER, DataType.BOOLEAN, DataType.JSON);
    }

    // Helper method
    private Configuration createAndSaveConfiguration(ConfigCategory category, String key, String value) {
        Configuration configuration = Configuration.builder()
            .category(category)
            .key(key)
            .value(value)
            .description("Test description")
            .dataType(DataType.STRING)
            .isEncrypted(false)
            .build();
        return configurationRepository.save(configuration);
    }
}
