package com.school.sms.configuration.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.sms.configuration.application.dto.request.CreateConfigurationRequest;
import com.school.sms.configuration.application.dto.response.ConfigurationResponse;
import com.school.sms.configuration.application.dto.response.GroupedConfigurationResponse;
import com.school.sms.configuration.application.service.ConfigurationApplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ConfigurationController using @WebMvcTest.
 *
 * Tests all REST endpoints with MockMvc:
 * - GET /api/v1/configurations - Get all configurations
 * - GET /api/v1/configurations/{category}/{key} - Get specific configuration
 * - PUT /api/v1/configurations/{category}/{key} - Create or update (UPSERT)
 * - DELETE /api/v1/configurations/{category}/{key} - Delete configuration
 * - GET /api/v1/configurations/grouped/{category} - Get grouped configurations
 */
@WebMvcTest(ConfigurationController.class)
@DisplayName("ConfigurationController Integration Tests")
class ConfigurationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConfigurationApplicationService configurationService;

    @Nested
    @DisplayName("GET /api/v1/configurations - Get All Configurations")
    class GetAllConfigurationsTests {

        @Test
        @DisplayName("Should retrieve all configurations without filter")
        void shouldRetrieveAllConfigurationsWithoutFilter() throws Exception {
            // Given
            List<ConfigurationResponse> expectedConfigurations = Arrays.asList(
                new ConfigurationResponse(
                    1L, "GENERAL", "school_name", "ABC School", "STRING",
                    "Name of the school", false, 0L, LocalDateTime.now(), "admin"
                ),
                new ConfigurationResponse(
                    2L, "ACADEMIC", "min_age", "3", "NUMBER",
                    "Minimum student age", false, 0L, LocalDateTime.now(), "admin"
                )
            );

            when(configurationService.getAllConfigurations(isNull()))
                .thenReturn(expectedConfigurations);

            // When & Then
            mockMvc.perform(get("/api/v1/configurations")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].category").value("GENERAL"))
                .andExpect(jsonPath("$[0].configKey").value("school_name"))
                .andExpect(jsonPath("$[1].category").value("ACADEMIC"))
                .andExpect(jsonPath("$[1].configKey").value("min_age"));

            verify(configurationService, times(1)).getAllConfigurations(isNull());
        }

        @Test
        @DisplayName("Should retrieve configurations filtered by category")
        void shouldRetrieveConfigurationsFilteredByCategory() throws Exception {
            // Given
            String category = "GENERAL";
            List<ConfigurationResponse> expectedConfigurations = Arrays.asList(
                new ConfigurationResponse(
                    1L, "GENERAL", "school_name", "ABC School", "STRING",
                    "Name of the school", false, 0L, LocalDateTime.now(), "admin"
                ),
                new ConfigurationResponse(
                    2L, "GENERAL", "school_address", "123 Main St", "STRING",
                    "School address", false, 0L, LocalDateTime.now(), "admin"
                )
            );

            when(configurationService.getAllConfigurations(eq(category)))
                .thenReturn(expectedConfigurations);

            // When & Then
            mockMvc.perform(get("/api/v1/configurations")
                    .param("category", category)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].category").value("GENERAL"))
                .andExpect(jsonPath("$[1].category").value("GENERAL"));

            verify(configurationService, times(1)).getAllConfigurations(eq(category));
        }

        @Test
        @DisplayName("Should return empty list when no configurations exist")
        void shouldReturnEmptyListWhenNoConfigurations() throws Exception {
            // Given
            when(configurationService.getAllConfigurations(isNull()))
                .thenReturn(Arrays.asList());

            // When & Then
            mockMvc.perform(get("/api/v1/configurations")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

            verify(configurationService, times(1)).getAllConfigurations(isNull());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/configurations/{category}/{key} - Get Specific Configuration")
    class GetConfigurationTests {

        @Test
        @DisplayName("Should retrieve specific configuration by category and key")
        void shouldRetrieveConfigurationByCategoryAndKey() throws Exception {
            // Given
            String category = "GENERAL";
            String key = "school_name";
            ConfigurationResponse expectedResponse = new ConfigurationResponse(
                1L, category, key, "ABC School", "STRING",
                "Name of the school", false, 0L, LocalDateTime.now(), "admin"
            );

            when(configurationService.getConfiguration(eq(category), eq(key)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/configurations/{category}/{key}", category, key)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.category").value(category))
                .andExpect(jsonPath("$.configKey").value(key))
                .andExpect(jsonPath("$.configValue").value("ABC School"))
                .andExpect(jsonPath("$.dataType").value("STRING"));

            verify(configurationService, times(1)).getConfiguration(eq(category), eq(key));
        }

        @Test
        @DisplayName("Should handle configuration not found scenario")
        void shouldHandleConfigurationNotFound() throws Exception {
            // Given
            String category = "GENERAL";
            String key = "non_existent_key";
            when(configurationService.getConfiguration(eq(category), eq(key)))
                .thenThrow(new RuntimeException("Configuration not found"));

            // When & Then
            mockMvc.perform(get("/api/v1/configurations/{category}/{key}", category, key)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

            verify(configurationService, times(1)).getConfiguration(eq(category), eq(key));
        }

        @Test
        @DisplayName("Should retrieve encrypted configuration")
        void shouldRetrieveEncryptedConfiguration() throws Exception {
            // Given
            String category = "GENERAL";
            String key = "api_secret";
            ConfigurationResponse expectedResponse = new ConfigurationResponse(
                1L, category, key, "encrypted_value", "STRING",
                "API secret key", true, 0L, LocalDateTime.now(), "admin"
            );

            when(configurationService.getConfiguration(eq(category), eq(key)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/configurations/{category}/{key}", category, key)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isEncrypted").value(true))
                .andExpect(jsonPath("$.configValue").value("encrypted_value"));

            verify(configurationService, times(1)).getConfiguration(eq(category), eq(key));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/configurations/{category}/{key} - Create or Update Configuration")
    class CreateOrUpdateConfigurationTests {

        @Test
        @DisplayName("Should create new configuration successfully")
        void shouldCreateNewConfigurationSuccessfully() throws Exception {
            // Given
            String category = "GENERAL";
            String key = "school_name";
            CreateConfigurationRequest request = new CreateConfigurationRequest(
                "ABC School",
                "STRING",
                "Name of the school",
                false,
                "admin"
            );

            ConfigurationResponse expectedResponse = new ConfigurationResponse(
                1L, category, key, "ABC School", "STRING",
                "Name of the school", false, 0L, LocalDateTime.now(), "admin"
            );

            when(configurationService.createOrUpdate(eq(category), eq(key), any(CreateConfigurationRequest.class)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/configurations/{category}/{key}", category, key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.category").value(category))
                .andExpect(jsonPath("$.configKey").value(key))
                .andExpect(jsonPath("$.configValue").value("ABC School"))
                .andExpect(jsonPath("$.dataType").value("STRING"))
                .andExpect(jsonPath("$.version").value(0));

            verify(configurationService, times(1)).createOrUpdate(eq(category), eq(key), any(CreateConfigurationRequest.class));
        }

        @Test
        @DisplayName("Should update existing configuration successfully")
        void shouldUpdateExistingConfigurationSuccessfully() throws Exception {
            // Given
            String category = "GENERAL";
            String key = "school_name";
            CreateConfigurationRequest request = new CreateConfigurationRequest(
                "XYZ School",
                "STRING",
                "Updated school name",
                false,
                "admin"
            );

            ConfigurationResponse expectedResponse = new ConfigurationResponse(
                1L, category, key, "XYZ School", "STRING",
                "Updated school name", false, 1L, LocalDateTime.now(), "admin"
            );

            when(configurationService.createOrUpdate(eq(category), eq(key), any(CreateConfigurationRequest.class)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/configurations/{category}/{key}", category, key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.configValue").value("XYZ School"))
                .andExpect(jsonPath("$.version").value(1));

            verify(configurationService, times(1)).createOrUpdate(eq(category), eq(key), any(CreateConfigurationRequest.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when configValue is blank")
        void shouldReturnBadRequestWhenConfigValueBlank() throws Exception {
            // Given
            String category = "GENERAL";
            String key = "school_name";
            CreateConfigurationRequest request = new CreateConfigurationRequest(
                "",  // Blank value
                "STRING",
                "Name of the school",
                false,
                "admin"
            );

            // When & Then
            mockMvc.perform(put("/api/v1/configurations/{category}/{key}", category, key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

            verify(configurationService, never()).createOrUpdate(anyString(), anyString(), any(CreateConfigurationRequest.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when dataType is null")
        void shouldReturnBadRequestWhenDataTypeNull() throws Exception {
            // Given
            String category = "GENERAL";
            String key = "school_name";
            CreateConfigurationRequest request = new CreateConfigurationRequest(
                "ABC School",
                null,  // Null dataType
                "Name of the school",
                false,
                "admin"
            );

            // When & Then
            mockMvc.perform(put("/api/v1/configurations/{category}/{key}", category, key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

            verify(configurationService, never()).createOrUpdate(anyString(), anyString(), any(CreateConfigurationRequest.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when updatedBy is blank")
        void shouldReturnBadRequestWhenUpdatedByBlank() throws Exception {
            // Given
            String category = "GENERAL";
            String key = "school_name";
            CreateConfigurationRequest request = new CreateConfigurationRequest(
                "ABC School",
                "STRING",
                "Name of the school",
                false,
                ""  // Blank updatedBy
            );

            // When & Then
            mockMvc.perform(put("/api/v1/configurations/{category}/{key}", category, key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

            verify(configurationService, never()).createOrUpdate(anyString(), anyString(), any(CreateConfigurationRequest.class));
        }

        @Test
        @DisplayName("Should create encrypted configuration successfully")
        void shouldCreateEncryptedConfigurationSuccessfully() throws Exception {
            // Given
            String category = "GENERAL";
            String key = "api_secret";
            CreateConfigurationRequest request = new CreateConfigurationRequest(
                "my_secret_key",
                "STRING",
                "API secret key",
                true,  // Encrypted
                "admin"
            );

            ConfigurationResponse expectedResponse = new ConfigurationResponse(
                1L, category, key, "encrypted_value", "STRING",
                "API secret key", true, 0L, LocalDateTime.now(), "admin"
            );

            when(configurationService.createOrUpdate(eq(category), eq(key), any(CreateConfigurationRequest.class)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/configurations/{category}/{key}", category, key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isEncrypted").value(true));

            verify(configurationService, times(1)).createOrUpdate(eq(category), eq(key), any(CreateConfigurationRequest.class));
        }

        @Test
        @DisplayName("Should create NUMBER data type configuration")
        void shouldCreateNumberDataTypeConfiguration() throws Exception {
            // Given
            String category = "ACADEMIC";
            String key = "min_age";
            CreateConfigurationRequest request = new CreateConfigurationRequest(
                "3",
                "NUMBER",
                "Minimum student age",
                false,
                "admin"
            );

            ConfigurationResponse expectedResponse = new ConfigurationResponse(
                1L, category, key, "3", "NUMBER",
                "Minimum student age", false, 0L, LocalDateTime.now(), "admin"
            );

            when(configurationService.createOrUpdate(eq(category), eq(key), any(CreateConfigurationRequest.class)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/configurations/{category}/{key}", category, key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataType").value("NUMBER"))
                .andExpect(jsonPath("$.configValue").value("3"));

            verify(configurationService, times(1)).createOrUpdate(eq(category), eq(key), any(CreateConfigurationRequest.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/configurations/{category}/{key} - Delete Configuration")
    class DeleteConfigurationTests {

        @Test
        @DisplayName("Should delete configuration successfully")
        void shouldDeleteConfigurationSuccessfully() throws Exception {
            // Given
            String category = "GENERAL";
            String key = "school_name";
            doNothing().when(configurationService).deleteConfiguration(category, key);

            // When & Then
            mockMvc.perform(delete("/api/v1/configurations/{category}/{key}", category, key))
                .andExpect(status().isNoContent());

            verify(configurationService, times(1)).deleteConfiguration(eq(category), eq(key));
        }

        @Test
        @DisplayName("Should handle configuration not found during deletion")
        void shouldHandleConfigurationNotFoundDuringDeletion() throws Exception {
            // Given
            String category = "GENERAL";
            String key = "non_existent_key";
            doThrow(new RuntimeException("Configuration not found"))
                .when(configurationService).deleteConfiguration(category, key);

            // When & Then
            mockMvc.perform(delete("/api/v1/configurations/{category}/{key}", category, key))
                .andExpect(status().is5xxServerError());

            verify(configurationService, times(1)).deleteConfiguration(eq(category), eq(key));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/configurations/grouped/{category} - Get Grouped Configurations")
    class GetGroupedConfigurationsTests {

        @Test
        @DisplayName("Should retrieve grouped configurations by category")
        void shouldRetrieveGroupedConfigurationsByCategory() throws Exception {
            // Given
            String category = "GENERAL";
            Map<String, String> configurations = new HashMap<>();
            configurations.put("school_name", "ABC School");
            configurations.put("school_address", "123 Main St");
            configurations.put("school_phone", "1234567890");

            GroupedConfigurationResponse expectedResponse = new GroupedConfigurationResponse(
                category,
                configurations
            );

            when(configurationService.getGroupedByCategory(eq(category)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/configurations/grouped/{category}", category)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.category").value(category))
                .andExpect(jsonPath("$.configurations.school_name").value("ABC School"))
                .andExpect(jsonPath("$.configurations.school_address").value("123 Main St"))
                .andExpect(jsonPath("$.configurations.school_phone").value("1234567890"));

            verify(configurationService, times(1)).getGroupedByCategory(eq(category));
        }

        @Test
        @DisplayName("Should return empty map when no configurations exist for category")
        void shouldReturnEmptyMapWhenNoConfigurationsExist() throws Exception {
            // Given
            String category = "FINANCIAL";
            GroupedConfigurationResponse expectedResponse = new GroupedConfigurationResponse(
                category,
                new HashMap<>()
            );

            when(configurationService.getGroupedByCategory(eq(category)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/configurations/grouped/{category}", category)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value(category))
                .andExpect(jsonPath("$.configurations").isEmpty());

            verify(configurationService, times(1)).getGroupedByCategory(eq(category));
        }

        @Test
        @DisplayName("Should retrieve grouped configurations for ACADEMIC category")
        void shouldRetrieveGroupedConfigurationsForAcademicCategory() throws Exception {
            // Given
            String category = "ACADEMIC";
            Map<String, String> configurations = new HashMap<>();
            configurations.put("min_age", "3");
            configurations.put("max_age", "18");
            configurations.put("academic_year_start", "2024-04-01");

            GroupedConfigurationResponse expectedResponse = new GroupedConfigurationResponse(
                category,
                configurations
            );

            when(configurationService.getGroupedByCategory(eq(category)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/configurations/grouped/{category}", category)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value(category))
                .andExpect(jsonPath("$.configurations.min_age").value("3"))
                .andExpect(jsonPath("$.configurations.max_age").value("18"));

            verify(configurationService, times(1)).getGroupedByCategory(eq(category));
        }
    }
}
