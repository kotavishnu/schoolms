package com.school.sms.student.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.sms.student.application.dto.StudentDTO;
import com.school.sms.student.application.service.StudentManagementService;
import com.school.sms.student.application.service.StudentRegistrationService;
import com.school.sms.student.application.service.StudentSearchService;
import com.school.sms.student.application.service.StudentStatusService;
import com.school.sms.student.domain.exception.DuplicateMobileException;
import com.school.sms.student.domain.exception.InvalidAgeException;
import com.school.sms.student.domain.exception.StudentNotFoundException;
import com.school.sms.student.domain.valueobject.StudentStatus;
import com.school.sms.student.presentation.dto.CreateStudentRequest;
import com.school.sms.student.presentation.dto.UpdateStatusRequest;
import com.school.sms.student.presentation.dto.UpdateStudentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for StudentController.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayName("Student Controller Integration Tests")
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentRegistrationService registrationService;

    @MockBean
    private StudentManagementService managementService;

    @MockBean
    private StudentSearchService searchService;

    @MockBean
    private StudentStatusService statusService;

    @Test
    @DisplayName("Should create student successfully")
    void shouldCreateStudentSuccessfully() throws Exception {
        // Given
        CreateStudentRequest request = CreateStudentRequest.builder()
                .firstName("Rajesh")
                .lastName("Kumar")
                .dateOfBirth(LocalDate.of(2015, 5, 15))
                .street("123 MG Road")
                .city("Bangalore")
                .state("Karnataka")
                .pinCode("560001")
                .mobile("9876543210")
                .email("rajesh@example.com")
                .fatherNameOrGuardian("Suresh Kumar")
                .build();

        StudentDTO response = StudentDTO.builder()
                .studentId("STU-2025-00001")
                .firstName("Rajesh")
                .lastName("Kumar")
                .status(StudentStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        when(registrationService.registerStudent(any(CreateStudentRequest.class), anyString()))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-ID", "ADMIN001")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value("STU-2025-00001"))
                .andExpect(jsonPath("$.firstName").value("Rajesh"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(registrationService, times(1))
                .registerStudent(any(CreateStudentRequest.class), eq("ADMIN001"));
    }

    @Test
    @DisplayName("Should return 400 when age is invalid")
    void shouldReturn400WhenAgeIsInvalid() throws Exception {
        // Given
        CreateStudentRequest request = CreateStudentRequest.builder()
                .firstName("Rajesh")
                .lastName("Kumar")
                .dateOfBirth(LocalDate.of(2022, 5, 15)) // Too young
                .street("123 MG Road")
                .city("Bangalore")
                .state("Karnataka")
                .pinCode("560001")
                .mobile("9876543210")
                .fatherNameOrGuardian("Suresh Kumar")
                .build();

        when(registrationService.registerStudent(any(CreateStudentRequest.class), anyString()))
                .thenThrow(new InvalidAgeException("Student age must be between 3 and 18 years. Provided age: 2"));

        // When & Then
        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-ID", "ADMIN001")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Age"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Should return 409 when mobile is duplicate")
    void shouldReturn409WhenMobileIsDuplicate() throws Exception {
        // Given
        CreateStudentRequest request = CreateStudentRequest.builder()
                .firstName("Rajesh")
                .lastName("Kumar")
                .dateOfBirth(LocalDate.of(2015, 5, 15))
                .street("123 MG Road")
                .city("Bangalore")
                .state("Karnataka")
                .pinCode("560001")
                .mobile("9876543210")
                .fatherNameOrGuardian("Suresh Kumar")
                .build();

        when(registrationService.registerStudent(any(CreateStudentRequest.class), anyString()))
                .thenThrow(new DuplicateMobileException("Mobile number already registered: 9876543210"));

        // When & Then
        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-ID", "ADMIN001")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Duplicate Mobile Number"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("Should return 400 when validation fails")
    void shouldReturn400WhenValidationFails() throws Exception {
        // Given - invalid request (missing required fields)
        CreateStudentRequest request = CreateStudentRequest.builder()
                .firstName("R") // Too short
                .build();

        // When & Then
        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-ID", "ADMIN001")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"));
    }

    @Test
    @DisplayName("Should get student by ID successfully")
    void shouldGetStudentByIdSuccessfully() throws Exception {
        // Given
        StudentDTO response = StudentDTO.builder()
                .studentId("STU-2025-00001")
                .firstName("Rajesh")
                .lastName("Kumar")
                .status(StudentStatus.ACTIVE)
                .build();

        when(searchService.findById("STU-2025-00001")).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/students/STU-2025-00001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value("STU-2025-00001"))
                .andExpect(jsonPath("$.firstName").value("Rajesh"));

        verify(searchService, times(1)).findById("STU-2025-00001");
    }

    @Test
    @DisplayName("Should return 404 when student not found")
    void shouldReturn404WhenStudentNotFound() throws Exception {
        // Given
        when(searchService.findById("STU-9999-99999"))
                .thenThrow(new StudentNotFoundException("Student not found: STU-9999-99999"));

        // When & Then
        mockMvc.perform(get("/students/STU-9999-99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Student Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Should update student successfully")
    void shouldUpdateStudentSuccessfully() throws Exception {
        // Given
        UpdateStudentRequest request = UpdateStudentRequest.builder()
                .version(0L)
                .firstName("Rajesh")
                .lastName("Kumar")
                .dateOfBirth(LocalDate.of(2015, 5, 15))
                .street("456 New Address")
                .city("Bangalore")
                .state("Karnataka")
                .pinCode("560025")
                .mobile("9876543210")
                .fatherNameOrGuardian("Suresh Kumar")
                .build();

        StudentDTO response = StudentDTO.builder()
                .studentId("STU-2025-00001")
                .firstName("Rajesh")
                .version(1L)
                .build();

        when(managementService.updateStudent(eq("STU-2025-00001"), any(UpdateStudentRequest.class), anyString()))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(put("/students/STU-2025-00001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-ID", "ADMIN001")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value("STU-2025-00001"))
                .andExpect(jsonPath("$.version").value(1));
    }

    @Test
    @DisplayName("Should return 409 on concurrent update")
    void shouldReturn409OnConcurrentUpdate() throws Exception {
        // Given
        UpdateStudentRequest request = UpdateStudentRequest.builder()
                .version(0L)
                .firstName("Rajesh")
                .lastName("Kumar")
                .dateOfBirth(LocalDate.of(2015, 5, 15))
                .street("456 New Address")
                .city("Bangalore")
                .state("Karnataka")
                .pinCode("560025")
                .mobile("9876543210")
                .fatherNameOrGuardian("Suresh Kumar")
                .build();

        when(managementService.updateStudent(anyString(), any(UpdateStudentRequest.class), anyString()))
                .thenThrow(new ObjectOptimisticLockingFailureException("Student", "STU-2025-00001"));

        // When & Then
        mockMvc.perform(put("/students/STU-2025-00001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-ID", "ADMIN001")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Concurrent Modification"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("Should delete student successfully")
    void shouldDeleteStudentSuccessfully() throws Exception {
        // Given
        doNothing().when(managementService).deleteStudent("STU-2025-00001");

        // When & Then
        mockMvc.perform(delete("/students/STU-2025-00001"))
                .andExpect(status().isNoContent());

        verify(managementService, times(1)).deleteStudent("STU-2025-00001");
    }

    @Test
    @DisplayName("Should search students with pagination")
    void shouldSearchStudentsWithPagination() throws Exception {
        // Given
        StudentDTO student1 = StudentDTO.builder()
                .studentId("STU-2025-00001")
                .firstName("Rajesh")
                .status(StudentStatus.ACTIVE)
                .build();

        when(searchService.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(student1), PageRequest.of(0, 20), 1));

        // When & Then
        mockMvc.perform(get("/students")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].studentId").value("STU-2025-00001"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Should update student status successfully")
    void shouldUpdateStudentStatusSuccessfully() throws Exception {
        // Given
        UpdateStatusRequest request = UpdateStatusRequest.builder()
                .status(StudentStatus.INACTIVE)
                .build();

        StudentDTO response = StudentDTO.builder()
                .studentId("STU-2025-00001")
                .status(StudentStatus.INACTIVE)
                .build();

        when(statusService.updateStatus(eq("STU-2025-00001"), eq(StudentStatus.INACTIVE), anyString()))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(patch("/students/STU-2025-00001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-ID", "ADMIN001")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }
}
