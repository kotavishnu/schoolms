package com.schoolms.student.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolms.student.domain.model.StudentStatus;
import com.schoolms.student.presentation.dto.request.CreateStudentRequest;
import com.schoolms.student.presentation.dto.request.UpdateStudentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for StudentController (QA-016 to QA-028)
 *
 * Tests all REST API endpoints with real HTTP requests/responses
 * Uses MockMvc for testing without starting a full HTTP server
 *
 * NOTE: This is a TEMPLATE for integration tests.
 * For full implementation, you need:
 * 1. TestContainers for PostgreSQL and Redis
 * 2. Test database initialization scripts
 * 3. Test-specific application.yml configuration
 *
 * Coverage:
 * - HTTP status code validation
 * - Request/response JSON structure
 * - Business rule enforcement at API level
 * - Error response format (RFC 7807 Problem Detail)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("StudentController Integration Tests (Template)")
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/students - Should create student and return 201 CREATED")
    void shouldCreateStudentSuccessfully() throws Exception {
        // Given
        CreateStudentRequest request = new CreateStudentRequest(
            "John",
            "Doe",
            LocalDate.now().minusYears(10),
            "123456789012",
            "9876543210",
            "john.doe@example.com",
            "123 Main Street",
            "Robert Doe",
            "Jane Doe",
            "Mole on left hand"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id", matchesPattern("^STU-\\d{4}-\\d{5}$")))
            .andExpect(jsonPath("$.firstName", is("John")))
            .andExpect(jsonPath("$.lastName", is("Doe")))
            .andExpect(jsonPath("$.status", is("ACTIVE")))
            .andExpect(jsonPath("$.age", is(10)))
            .andExpect(jsonPath("$.phone", is("9876543210")))
            .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    @DisplayName("POST /api/v1/students - Should return 409 CONFLICT for duplicate phone")
    void shouldRejectDuplicatePhone() throws Exception {
        // Given - Create first student
        CreateStudentRequest firstRequest = new CreateStudentRequest(
            "John", "Doe", LocalDate.now().minusYears(10),
            "123456789012", "9876543210", "john.doe@example.com",
            "123 Main Street", "Robert Doe", "Jane Doe", "Mole"
        );

        mockMvc.perform(post("/api/v1/students")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(firstRequest)))
            .andExpect(status().isCreated());

        // When - Try to create second student with same phone
        CreateStudentRequest duplicateRequest = new CreateStudentRequest(
            "Jane", "Smith", LocalDate.now().minusYears(12),
            "987654321098", "9876543210", "jane.smith@example.com",
            "456 Oak Avenue", "Michael Smith", "Sarah Smith", "Scar"
        );

        // Then
        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.type", notNullValue()))
            .andExpect(jsonPath("$.title", is("Conflict")))
            .andExpect(jsonPath("$.status", is(409)))
            .andExpect(jsonPath("$.detail", containsString("phone")));
    }

    @Test
    @DisplayName("POST /api/v1/students - Should return 422 UNPROCESSABLE ENTITY for invalid age")
    void shouldRejectInvalidAge() throws Exception {
        // Given - Student with age 2 (below minimum of 3)
        CreateStudentRequest request = new CreateStudentRequest(
            "Baby", "Doe", LocalDate.now().minusYears(2),
            "123456789012", "9876543210", "baby.doe@example.com",
            "123 Main Street", "Robert Doe", "Jane Doe", "None"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.status", is(422)))
            .andExpect(jsonPath("$.detail", containsString("Age")))
            .andExpect(jsonPath("$.detail", containsString("3-18")));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} - Should return 200 OK with student data")
    void shouldGetStudentById() throws Exception {
        // Given - Create a student first
        CreateStudentRequest request = new CreateStudentRequest(
            "John", "Doe", LocalDate.now().minusYears(10),
            "123456789012", "9876543210", "john.doe@example.com",
            "123 Main Street", "Robert Doe", "Jane Doe", "Mole"
        );

        String createResponse = mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String studentId = objectMapper.readTree(createResponse).get("id").asText();

        // When & Then
        mockMvc.perform(get("/api/v1/students/{id}", studentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(studentId)))
            .andExpect(jsonPath("$.firstName", is("John")))
            .andExpect(jsonPath("$.lastName", is("Doe")));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} - Should return 404 NOT FOUND for non-existent student")
    void shouldReturn404ForNonExistentStudent() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/students/{id}", "STU-2026-99999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    @DisplayName("PATCH /api/v1/students/{id} - Should update student and return 200 OK")
    void shouldUpdateStudent() throws Exception {
        // Given - Create a student first
        CreateStudentRequest createRequest = new CreateStudentRequest(
            "John", "Doe", LocalDate.now().minusYears(10),
            "123456789012", "9876543210", "john.doe@example.com",
            "123 Main Street", "Robert Doe", "Jane Doe", "Mole"
        );

        String createResponse = mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andReturn()
            .getResponse()
            .getContentAsString();

        String studentId = objectMapper.readTree(createResponse).get("id").asText();

        // When - Update the student
        UpdateStudentRequest updateRequest = new UpdateStudentRequest(
            "Jonathan",
            "Smith",
            "9123456789",
            null
        );

        // Then
        mockMvc.perform(patch("/api/v1/students/{id}", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Jonathan")))
            .andExpect(jsonPath("$.lastName", is("Smith")))
            .andExpect(jsonPath("$.phone", is("9123456789")));
    }

    @Test
    @DisplayName("DELETE /api/v1/students/{id} - Should delete student and return 204 NO CONTENT")
    void shouldDeleteStudent() throws Exception {
        // Given - Create a student first
        CreateStudentRequest request = new CreateStudentRequest(
            "John", "Doe", LocalDate.now().minusYears(10),
            "123456789012", "9876543210", "john.doe@example.com",
            "123 Main Street", "Robert Doe", "Jane Doe", "Mole"
        );

        String createResponse = mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn()
            .getResponse()
            .getContentAsString();

        String studentId = objectMapper.readTree(createResponse).get("id").asText();

        // When & Then - Delete the student
        mockMvc.perform(delete("/api/v1/students/{id}", studentId))
            .andExpect(status().isNoContent());

        // Verify student is deleted
        mockMvc.perform(get("/api/v1/students/{id}", studentId))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/students - Should list all students and return 200 OK")
    void shouldListAllStudents() throws Exception {
        // Given - Create multiple students
        CreateStudentRequest request1 = new CreateStudentRequest(
            "John", "Doe", LocalDate.now().minusYears(10),
            "123456789012", "9876543210", "john.doe@example.com",
            "123 Main Street", "Robert Doe", "Jane Doe", "Mole"
        );

        CreateStudentRequest request2 = new CreateStudentRequest(
            "Jane", "Smith", LocalDate.now().minusYears(12),
            "987654321098", "9123456789", "jane.smith@example.com",
            "456 Oak Avenue", "Michael Smith", "Sarah Smith", "Scar"
        );

        mockMvc.perform(post("/api/v1/students")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request1)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/students")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request2)))
            .andExpect(status().isCreated());

        // When & Then
        mockMvc.perform(get("/api/v1/students"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.students", hasSize(greaterThanOrEqualTo(2))))
            .andExpect(jsonPath("$.totalCount", greaterThanOrEqualTo(2)))
            .andExpect(jsonPath("$.activeCount", greaterThanOrEqualTo(2)))
            .andExpect(jsonPath("$.inactiveCount", is(0)));
    }

    @Test
    @DisplayName("GET /api/v1/students?status=ACTIVE - Should filter by status and return 200 OK")
    void shouldFilterStudentsByStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/students")
                .param("status", "ACTIVE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.students", isA(java.util.List.class)));
    }

    @Test
    @DisplayName("GET /api/v1/students?search=John - Should search students and return 200 OK")
    void shouldSearchStudents() throws Exception {
        // Given - Create a student with searchable name
        CreateStudentRequest request = new CreateStudentRequest(
            "Jonathan", "Doe", LocalDate.now().minusYears(10),
            "123456789012", "9876543210", "jonathan.doe@example.com",
            "123 Main Street", "Robert Doe", "Jane Doe", "Mole"
        );

        mockMvc.perform(post("/api/v1/students")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        // When & Then
        mockMvc.perform(get("/api/v1/students")
                .param("search", "Jonathan"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.students[0].firstName", is("Jonathan")));
    }

    @Test
    @DisplayName("GET /api/v1/students/statistics - Should return statistics and 200 OK")
    void shouldGetStatistics() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/students/statistics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalStudents", isA(Integer.class)))
            .andExpect(jsonPath("$.activeStudents", isA(Integer.class)))
            .andExpect(jsonPath("$.inactiveStudents", isA(Integer.class)));
    }

    @Test
    @DisplayName("POST /api/v1/students - Should return 400 BAD REQUEST for invalid request body")
    void shouldRejectInvalidRequestBody() throws Exception {
        // Given - Invalid request (missing required fields)
        String invalidJson = "{\"firstName\": \"John\"}";

        // When & Then
        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }
}
