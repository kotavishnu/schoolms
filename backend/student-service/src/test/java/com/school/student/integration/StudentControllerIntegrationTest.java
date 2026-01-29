package com.school.student.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.student.controller.dto.request.StudentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Student API
 * Uses TestContainers for PostgreSQL and Redis
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Student API Integration Tests")
class StudentControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
        .withDatabaseName("student_db_test")
        .withUsername("test")
        .withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl() + "?TimeZone=UTC");
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create student successfully via API")
    void shouldCreateStudentSuccessfully() throws Exception {
        StudentRequest request = StudentRequest.builder()
            .firstName("Integration")
            .lastName("Test")
            .dateOfBirth(LocalDate.of(2010, 1, 1))
            .mobile("9999999999")
            .email("integration@test.com")
            .build();

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.studentId").exists())
            .andExpect(jsonPath("$.firstName").value("Integration"))
            .andExpect(jsonPath("$.lastName").value("Test"));
    }

    @Test
    @DisplayName("Should return 400 for invalid student data")
    void shouldReturn400ForInvalidData() throws Exception {
        StudentRequest request = StudentRequest.builder()
            .firstName("A") // Too short
            .lastName("Test")
            .dateOfBirth(LocalDate.now().plusDays(1)) // Future date
            .mobile("123") // Invalid format
            .build();

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 404 when student not found")
    void shouldReturn404WhenStudentNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/students/STD-99999999-9999"))
            .andExpect(status().isNotFound());
    }
}
