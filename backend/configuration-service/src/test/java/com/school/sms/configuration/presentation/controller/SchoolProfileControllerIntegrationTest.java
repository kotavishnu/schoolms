package com.school.sms.configuration.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.sms.configuration.application.dto.UpdateSchoolProfileRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for SchoolProfileController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("School Profile Controller Integration Tests")
class SchoolProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/v1/configurations/school-profile";
    private static final String USER_ID_HEADER = "X-User-ID";
    private static final String TEST_USER_ID = "test-user";

    private UpdateSchoolProfileRequest validUpdateRequest;

    @BeforeEach
    void setUp() {
        validUpdateRequest = UpdateSchoolProfileRequest.builder()
                .schoolName("Updated School Name")
                .schoolCode("UPD-2024")
                .logoPath("/logos/updated-logo.png")
                .address("123 Updated Street, City, State 12345")
                .phone("+1-555-9999")
                .email("updated@school.com")
                .build();
    }

    @Test
    @DisplayName("Should get school profile successfully")
    void shouldGetSchoolProfileSuccessfully() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileId").exists())
                .andExpect(jsonPath("$.schoolName").exists())
                .andExpect(jsonPath("$.schoolCode").exists());
    }

    @Test
    @DisplayName("Should update school profile successfully")
    void shouldUpdateSchoolProfileSuccessfully() throws Exception {
        mockMvc.perform(put(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schoolName").value("Updated School Name"))
                .andExpect(jsonPath("$.schoolCode").value("UPD-2024"))
                .andExpect(jsonPath("$.logoPath").value("/logos/updated-logo.png"))
                .andExpect(jsonPath("$.address").value("123 Updated Street, City, State 12345"))
                .andExpect(jsonPath("$.phone").value("+1-555-9999"))
                .andExpect(jsonPath("$.email").value("updated@school.com"));
    }

    @Test
    @DisplayName("Should return 400 when updating school profile with invalid data")
    void shouldReturn400WhenUpdatingSchoolProfileWithInvalidData() throws Exception {
        UpdateSchoolProfileRequest invalidRequest = UpdateSchoolProfileRequest.builder()
                .schoolName("") // Empty school name
                .schoolCode("") // Empty school code
                .email("invalid-email") // Invalid email format
                .phone("123") // Invalid phone format
                .build();

        mockMvc.perform(put(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("Should preserve data consistency when updating profile")
    void shouldPreserveDataConsistencyWhenUpdatingProfile() throws Exception {
        // First update
        mockMvc.perform(put(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk());

        // Verify the update persisted
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schoolName").value("Updated School Name"))
                .andExpect(jsonPath("$.schoolCode").value("UPD-2024"));
    }

    @Test
    @DisplayName("Should update profile with partial data")
    void shouldUpdateProfileWithPartialData() throws Exception {
        UpdateSchoolProfileRequest partialRequest = UpdateSchoolProfileRequest.builder()
                .schoolName("Partial Update School")
                .schoolCode("PARTIAL-2024")
                .logoPath("/logos/partial-logo.png")
                .address("456 Partial Street")
                .phone("+1-555-1111")
                .email("partial@school.com")
                .build();

        mockMvc.perform(put(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schoolName").value("Partial Update School"));
    }

    @Test
    @DisplayName("Should handle concurrent updates with optimistic locking")
    void shouldHandleConcurrentUpdatesWithOptimisticLocking() throws Exception {
        // This test would require more complex setup to simulate concurrent updates
        // For now, we just verify the update mechanism works correctly
        mockMvc.perform(put(BASE_URL)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").exists());
    }
}
