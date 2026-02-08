package com.school.student.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.student.presentation.dto.StudentRequestDTO;
import com.school.student.presentation.dto.StudentResponseDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Student API using TestContainers.
 * Tests complete request-response flow with real PostgreSQL database.
 *
 * <p>Features:
 * <ul>
 *   <li>Uses TestContainers to spin up PostgreSQL</li>
 *   <li>Tests full application context</li>
 *   <li>Validates database constraints</li>
 *   <li>Tests Drools rule engine integration</li>
 *   <li>Verifies HTTP status codes and response structure</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Tag("integration")
@DisplayName("Student API Integration Tests")
class StudentIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
            .withDatabaseName("student_db_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Configure Spring to use TestContainers PostgreSQL.
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    // ==================== Complete Registration Flow ====================

    @Test
    @DisplayName("Integration: Complete student registration flow with database")
    void shouldRegisterStudentEndToEnd() throws Exception {
        // Arrange
        StudentRequestDTO request = StudentRequestDTO.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .dateOfBirth(LocalDate.of(2015, 3, 20))
                .mobile("9876543210")
                .email("alice.johnson@example.com")
                .address("456 Oak Street")
                .fathersName("Robert Johnson")
                .mothersName("Mary Johnson")
                .identificationMark("Scar on right knee")
                .aadhaarNumber("123456789012")
                .build();

        // Act & Assert - Register student
        MvcResult result = mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.studentId").exists())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Johnson"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.version").value(0))
                .andReturn();

        // Extract student ID from response
        String responseBody = result.getResponse().getContentAsString();
        StudentResponseDTO response = objectMapper.readValue(responseBody, StudentResponseDTO.class);
        String studentId = response.studentId();

        assertThat(studentId).isNotNull();
        assertThat(studentId).matches("STD-\\d{8}-\\d{4}");

        // Verify student can be retrieved
        mockMvc.perform(get("/api/v1/students/" + studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(studentId))
                .andExpect(jsonPath("$.mobile").value("9876543210"));
    }

    // ==================== Business Rule Validation (BR-1: Age) ====================

    @Test
    @DisplayName("Integration: BR-1 - Reject student under 3 years old")
    void shouldRejectStudentUnder3Years() throws Exception {
        // Arrange - Student age 2 years
        StudentRequestDTO request = StudentRequestDTO.builder()
                .firstName("Baby")
                .lastName("Smith")
                .dateOfBirth(LocalDate.now().minusYears(2))
                .mobile("9876543211")
                .fathersName("John Smith")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.code == 'AGE_OUT_OF_RANGE')]").exists());
    }

    @Test
    @DisplayName("Integration: BR-1 - Reject student over 18 years old")
    void shouldRejectStudentOver18Years() throws Exception {
        // Arrange - Student age 19 years
        StudentRequestDTO request = StudentRequestDTO.builder()
                .firstName("Old")
                .lastName("Student")
                .dateOfBirth(LocalDate.now().minusYears(19))
                .mobile("9876543212")
                .fathersName("Parent Name")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.code == 'AGE_OUT_OF_RANGE')]").exists());
    }

    // ==================== Business Rule Validation (BR-2: Mobile Uniqueness) ====================

    @Test
    @DisplayName("Integration: BR-2 - Reject duplicate mobile number")
    void shouldRejectDuplicateMobile() throws Exception {
        // Arrange - Register first student
        StudentRequestDTO firstStudent = StudentRequestDTO.builder()
                .firstName("First")
                .lastName("Student")
                .dateOfBirth(LocalDate.of(2015, 1, 1))
                .mobile("9999999999")
                .fathersName("Parent One")
                .build();

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstStudent)))
                .andExpect(status().isCreated());

        // Act - Try to register second student with same mobile
        StudentRequestDTO secondStudent = StudentRequestDTO.builder()
                .firstName("Second")
                .lastName("Student")
                .dateOfBirth(LocalDate.of(2016, 1, 1))
                .mobile("9999999999") // Same mobile
                .mothersName("Parent Two")
                .build();

        // Assert - Should be rejected
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondStudent)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.code == 'MOBILE_ALREADY_EXISTS')]").exists());
    }

    // ==================== Business Rule Validation (BR-5: Guardian Required) ====================

    @Test
    @DisplayName("Integration: BR-5 - Reject student without guardian")
    void shouldRejectStudentWithoutGuardian() throws Exception {
        // Arrange - No father's name or mother's name
        String invalidRequest = """
                {
                    "firstName": "NoGuardian",
                    "lastName": "Student",
                    "dateOfBirth": "2015-05-15",
                    "mobile": "8888888888"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    // ==================== Update Operations ====================

    @Test
    @DisplayName("Integration: Update student profile successfully")
    void shouldUpdateStudentProfile() throws Exception {
        // Arrange - Register student first
        StudentRequestDTO request = StudentRequestDTO.builder()
                .firstName("Original")
                .lastName("Name")
                .dateOfBirth(LocalDate.of(2015, 6, 15))
                .mobile("7777777777")
                .fathersName("Father Name")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        StudentResponseDTO created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                StudentResponseDTO.class
        );

        // Act - Update student
        String updateRequest = String.format("""
                {
                    "version": %d,
                    "firstName": "Updated",
                    "email": "updated.email@example.com"
                }
                """, created.version());

        // Assert - Update successful
        mockMvc.perform(put("/api/v1/students/" + created.studentId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated.email@example.com"))
                .andExpect(jsonPath("$.version").value(1)); // Version incremented
    }

    @Test
    @DisplayName("Integration: Optimistic locking - reject update with stale version")
    void shouldRejectUpdateWithStaleVersion() throws Exception {
        // Arrange - Register student
        StudentRequestDTO request = StudentRequestDTO.builder()
                .firstName("VersionTest")
                .lastName("Student")
                .dateOfBirth(LocalDate.of(2015, 7, 20))
                .mobile("6666666666")
                .fathersName("Father Name")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        StudentResponseDTO created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                StudentResponseDTO.class
        );

        // Act - Try to update with wrong version
        String updateRequest = """
                {
                    "version": 999,
                    "firstName": "ShouldFail"
                }
                """;

        // Assert - Should return 409 Conflict
        mockMvc.perform(put("/api/v1/students/" + created.studentId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Optimistic Locking Conflict"));
    }

    // ==================== Delete Operations ====================

    @Test
    @DisplayName("Integration: Delete student successfully")
    void shouldDeleteStudent() throws Exception {
        // Arrange - Register student
        StudentRequestDTO request = StudentRequestDTO.builder()
                .firstName("ToDelete")
                .lastName("Student")
                .dateOfBirth(LocalDate.of(2015, 8, 10))
                .mobile("5555555555")
                .mothersName("Mother Name")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        StudentResponseDTO created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                StudentResponseDTO.class
        );

        // Act - Delete student
        mockMvc.perform(delete("/api/v1/students/" + created.studentId()))
                .andExpect(status().isNoContent());

        // Assert - Student no longer exists
        mockMvc.perform(get("/api/v1/students/" + created.studentId()))
                .andExpect(status().isNotFound());
    }

    // ==================== Search and Pagination ====================

    @Test
    @DisplayName("Integration: Search students by last name")
    void shouldSearchStudentsByLastName() throws Exception {
        // Arrange - Register multiple students with same last name
        String lastName = "SearchTest" + System.currentTimeMillis();

        for (int i = 0; i < 3; i++) {
            StudentRequestDTO request = StudentRequestDTO.builder()
                    .firstName("Student" + i)
                    .lastName(lastName)
                    .dateOfBirth(LocalDate.of(2015, 1, i + 1))
                    .mobile("444444444" + i)
                    .fathersName("Father" + i)
                    .build();

            mockMvc.perform(post("/api/v1/students")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        // Act & Assert - Search by last name
        mockMvc.perform(get("/api/v1/students")
                        .param("lastName", lastName)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].lastName").value(lastName));
    }
}
