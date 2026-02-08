package com.school.student.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.student.application.service.StudentService;
import com.school.student.domain.model.StudentStatus;
import com.school.student.presentation.dto.StudentRequestDTO;
import com.school.student.presentation.dto.StudentResponseDTO;
import com.school.student.presentation.dto.StudentUpdateRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for StudentController using MockMvc.
 * Tests REST endpoint behavior without starting full server.
 */
@WebMvcTest(StudentController.class)
@DisplayName("StudentController REST API Tests")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    // Test data
    private final StudentRequestDTO validRequest = StudentRequestDTO.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .email("john.doe@example.com")
            .address("123 Main St")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .identificationMark("Mole on left arm")
            .aadhaarNumber("123456789012")
            .build();

    private final StudentResponseDTO validResponse = StudentResponseDTO.builder()
            .id(1L)
            .studentId("STD-20260203-0001")
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .email("john.doe@example.com")
            .status("ACTIVE")
            .version(0L)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    // ==================== POST /api/v1/students ====================

    @Test
    @DisplayName("POST /api/v1/students should return 201 with Location header")
    void shouldCreateStudentSuccessfully() throws Exception {
        // Arrange
        when(studentService.registerStudent(any(StudentRequestDTO.class)))
                .thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/v1/students/STD-20260203-0001")))
                .andExpect(jsonPath("$.studentId").value("STD-20260203-0001"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(studentService).registerStudent(any(StudentRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/students should return 400 for missing required field")
    void shouldReturn400ForMissingFirstName() throws Exception {
        // Arrange - create invalid request
        String invalidRequest = """
                {
                    "lastName": "Doe",
                    "dateOfBirth": "2015-05-15",
                    "mobile": "9876543210",
                    "fathersName": "James Doe"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/students should return 400 for invalid mobile format")
    void shouldReturn400ForInvalidMobileFormat() throws Exception {
        // Arrange
        String invalidRequest = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "dateOfBirth": "2015-05-15",
                    "mobile": "12345",
                    "fathersName": "James Doe"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    // ==================== GET /api/v1/students/{studentId} ====================

    @Test
    @DisplayName("GET /api/v1/students/{studentId} should return 200 with student data")
    void shouldGetStudentByIdSuccessfully() throws Exception {
        // Arrange
        when(studentService.getStudentById("STD-20260203-0001"))
                .thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/students/STD-20260203-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value("STD-20260203-0001"))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(studentService).getStudentById("STD-20260203-0001");
    }

    // ==================== PUT /api/v1/students/{studentId} ====================

    @Test
    @DisplayName("PUT /api/v1/students/{studentId} should return 200 on successful update")
    void shouldUpdateStudentSuccessfully() throws Exception {
        // Arrange
        StudentUpdateRequestDTO updateRequest = StudentUpdateRequestDTO.builder()
                .version(0L)
                .firstName("Johnny")
                .email("johnny.doe@example.com")
                .build();

        StudentResponseDTO updatedResponse = StudentResponseDTO.builder()
                .id(1L)
                .studentId("STD-20260203-0001")
                .firstName("Johnny")
                .lastName("Doe")
                .email("johnny.doe@example.com")
                .status("ACTIVE")
                .version(1L)
                .build();

        when(studentService.updateStudent(eq("STD-20260203-0001"), any(StudentUpdateRequestDTO.class)))
                .thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/students/STD-20260203-0001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Johnny"))
                .andExpect(jsonPath("$.version").value(1));

        verify(studentService).updateStudent(eq("STD-20260203-0001"), any(StudentUpdateRequestDTO.class));
    }

    // ==================== DELETE /api/v1/students/{studentId} ====================

    @Test
    @DisplayName("DELETE /api/v1/students/{studentId} should return 204")
    void shouldDeleteStudentSuccessfully() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/students/STD-20260203-0001"))
                .andExpect(status().isNoContent());

        verify(studentService).deleteStudent("STD-20260203-0001");
    }

    // ==================== GET /api/v1/students (search/list) ====================

    @Test
    @DisplayName("GET /api/v1/students should return paginated list")
    void shouldGetAllStudentsWithPagination() throws Exception {
        // Arrange
        Page<StudentResponseDTO> page = new PageImpl<>(
                List.of(validResponse),
                PageRequest.of(0, 20),
                1
        );

        when(studentService.getAllStudents(any(PageRequest.class)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/students")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].studentId").value("STD-20260203-0001"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/students?lastName=Doe should search by last name")
    void shouldSearchByLastName() throws Exception {
        // Arrange
        Page<StudentResponseDTO> page = new PageImpl<>(
                List.of(validResponse),
                PageRequest.of(0, 20),
                1
        );

        when(studentService.searchByLastName(eq("Doe"), any(PageRequest.class)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/students")
                        .param("lastName", "Doe")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].lastName").value("Doe"));

        verify(studentService).searchByLastName(eq("Doe"), any(PageRequest.class));
    }

    @Test
    @DisplayName("GET /api/v1/students?status=ACTIVE should filter by status")
    void shouldFilterByStatus() throws Exception {
        // Arrange
        Page<StudentResponseDTO> page = new PageImpl<>(
                List.of(validResponse),
                PageRequest.of(0, 20),
                1
        );

        when(studentService.findByStatus(eq(StudentStatus.ACTIVE), any(PageRequest.class)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/students")
                        .param("status", "ACTIVE")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));

        verify(studentService).findByStatus(eq(StudentStatus.ACTIVE), any(PageRequest.class));
    }
}
