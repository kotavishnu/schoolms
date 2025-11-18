package com.school.sms.configuration.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.sms.configuration.application.dto.CreateSettingRequest;
import com.school.sms.configuration.application.dto.UpdateSettingRequest;
import com.school.sms.configuration.domain.enums.SettingCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ConfigurationController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Configuration Controller Integration Tests")
class ConfigurationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/v1/configurations/settings";
    private static final String USER_ID_HEADER = "X-User-ID";
    private static final String TEST_USER_ID = "test-user";

    private CreateSettingRequest validCreateRequest;
    private UpdateSettingRequest validUpdateRequest;

    @BeforeEach
    void setUp() {
        validCreateRequest = CreateSettingRequest.builder()
                .category(SettingCategory.GENERAL)
                .key("TEST_SETTING")
                .value("Test Value")
                .description("Test Description")
                .build();

        validUpdateRequest = UpdateSettingRequest.builder()
                .value("Updated Value")
                .description("Updated Description")
                .build();
    }

    @Test
    @DisplayName("Should create setting successfully")
    void shouldCreateSettingSuccessfully() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.settingId").exists())
                .andExpect(jsonPath("$.category").value(SettingCategory.GENERAL.name()))
                .andExpect(jsonPath("$.key").value("TEST_SETTING"))
                .andExpect(jsonPath("$.value").value("Test Value"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    @DisplayName("Should return 400 when creating setting with invalid data")
    void shouldReturn400WhenCreatingSettingWithInvalidData() throws Exception {
        CreateSettingRequest invalidRequest = CreateSettingRequest.builder()
                .category(null) // Missing required field
                .key("")       // Empty key
                .value("")     // Empty value
                .build();

        mockMvc.perform(post(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("Should return 409 when creating duplicate setting")
    void shouldReturn409WhenCreatingDuplicateSetting() throws Exception {
        // Create first setting
        mockMvc.perform(post(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated());

        // Attempt to create duplicate
        mockMvc.perform(post(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Duplicate Setting"));
    }

    @Test
    @DisplayName("Should get setting by ID successfully")
    void shouldGetSettingByIdSuccessfully() throws Exception {
        // Create setting first
        String createResponse = mockMvc.perform(post(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long settingId = objectMapper.readTree(createResponse).get("settingId").asLong();

        // Get setting by ID
        mockMvc.perform(get(BASE_URL + "/{settingId}", settingId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.settingId").value(settingId))
                .andExpect(jsonPath("$.key").value("TEST_SETTING"));
    }

    @Test
    @DisplayName("Should return 404 when getting non-existent setting")
    void shouldReturn404WhenGettingNonExistentSetting() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{settingId}", 99999L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Setting Not Found"));
    }

    @Test
    @DisplayName("Should update setting successfully")
    void shouldUpdateSettingSuccessfully() throws Exception {
        // Create setting first
        String createResponse = mockMvc.perform(post(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long settingId = objectMapper.readTree(createResponse).get("settingId").asLong();

        // Update setting
        mockMvc.perform(put(BASE_URL + "/{settingId}", settingId)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("Updated Value"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    @DisplayName("Should delete setting successfully")
    void shouldDeleteSettingSuccessfully() throws Exception {
        // Create setting first
        String createResponse = mockMvc.perform(post(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long settingId = objectMapper.readTree(createResponse).get("settingId").asLong();

        // Delete setting
        mockMvc.perform(delete(BASE_URL + "/{settingId}", settingId))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get(BASE_URL + "/{settingId}", settingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get settings by category successfully")
    void shouldGetSettingsByCategorySuccessfully() throws Exception {
        // Create multiple settings in different categories
        CreateSettingRequest generalRequest = CreateSettingRequest.builder()
                .category(SettingCategory.GENERAL)
                .key("GENERAL_SETTING_1")
                .value("Value 1")
                .description("Description 1")
                .build();

        CreateSettingRequest academicRequest = CreateSettingRequest.builder()
                .category(SettingCategory.ACADEMIC)
                .key("ACADEMIC_SETTING_1")
                .value("Value 2")
                .description("Description 2")
                .build();

        mockMvc.perform(post(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(generalRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(academicRequest)))
                .andExpect(status().isCreated());

        // Get settings by category
        mockMvc.perform(get(BASE_URL + "/category/{category}", SettingCategory.GENERAL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value(SettingCategory.GENERAL.name()))
                .andExpect(jsonPath("$.settings").isArray())
                .andExpect(jsonPath("$.settings", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Should return 400 when getting settings with invalid category")
    void shouldReturn400WhenGettingSettingsWithInvalidCategory() throws Exception {
        mockMvc.perform(get(BASE_URL + "/category/{category}", "INVALID_CATEGORY"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Parameter"));
    }

    @Test
    @DisplayName("Should get all settings grouped by category successfully")
    void shouldGetAllSettingsGroupedByCategorySuccessfully() throws Exception {
        // Create settings in different categories
        CreateSettingRequest generalRequest = CreateSettingRequest.builder()
                .category(SettingCategory.GENERAL)
                .key("GENERAL_SETTING_2")
                .value("Value 1")
                .description("Description 1")
                .build();

        CreateSettingRequest financialRequest = CreateSettingRequest.builder()
                .category(SettingCategory.FINANCIAL)
                .key("FINANCIAL_SETTING_1")
                .value("Value 2")
                .description("Description 2")
                .build();

        mockMvc.perform(post(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(generalRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(financialRequest)))
                .andExpect(status().isCreated());

        // Get all settings grouped by category
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }
}
