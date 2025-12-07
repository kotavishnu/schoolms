package com.school.sms.student.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.sms.student.application.service.StudentApplicationService;
import com.school.sms.student.presentation.dto.request.CreateStudentRequest;
import com.school.sms.student.presentation.dto.request.UpdateStudentRequest;
import com.school.sms.student.presentation.dto.response.PagedStudentResponse;
import com.school.sms.student.presentation.dto.response.StudentResponse;
import com.school.sms.student.presentation.dto.response.StudentSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for StudentController using @WebMvcTest.
 *
 * Tests all REST endpoints with MockMvc:
 * - POST /api/v1/students - Create student
 * - GET /api/v1/students/{studentId} - Get student by ID
 * - PUT /api/v1/students/{studentId} - Update student
 * - DELETE /api/v1/students/{studentId} - Delete student
 * - GET /api/v1/students - Search students with filters
 */
@WebMvcTest(StudentController.class)
@DisplayName("StudentController Integration Tests")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentApplicationService studentService;

    @Nested
    @DisplayName("POST /api/v1/students - Create Student")
    class CreateStudentTests {

        @Test
        @DisplayName("Should create student successfully with valid request")
        void shouldCreateStudentSuccessfully() throws Exception {
            // Given
            CreateStudentRequest request = new CreateStudentRequest(
                "John",
                "Doe",
                LocalDate.of(2015, 5, 15),
                "1234567890",
                "john.doe@example.com",
                "123 Main St",
                "Robert Doe",
                "Jane Doe",
                "Mole on left arm",
                "123456789012"
            );

            StudentResponse expectedResponse = new StudentResponse(
                1L,
                "STD-20241206-0001",
                "John",
                "Doe",
                LocalDate.of(2015, 5, 15),
                9,
                "1234567890",
                "john.doe@example.com",
                "123 Main St",
                "Robert Doe",
                "Jane Doe",
                "Mole on left arm",
                "123456789012",
                "ACTIVE",
                0L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "system",
                "system"
            );

            when(studentService.createStudent(any(CreateStudentRequest.class)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.studentId").value("STD-20241206-0001"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.mobile").value("1234567890"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

            verify(studentService, times(1)).createStudent(any(CreateStudentRequest.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when firstName is blank")
        void shouldReturnBadRequestWhenFirstNameBlank() throws Exception {
            // Given
            CreateStudentRequest request = new CreateStudentRequest(
                "",  // Blank firstName
                "Doe",
                LocalDate.of(2015, 5, 15),
                "1234567890",
                null,
                null,
                null,
                null,
                null,
                null
            );

            // When & Then
            mockMvc.perform(post("/api/v1/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

            verify(studentService, never()).createStudent(any(CreateStudentRequest.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when mobile number is invalid")
        void shouldReturnBadRequestWhenMobileInvalid() throws Exception {
            // Given
            CreateStudentRequest request = new CreateStudentRequest(
                "John",
                "Doe",
                LocalDate.of(2015, 5, 15),
                "12345",  // Invalid mobile (less than 10 digits)
                null,
                null,
                null,
                null,
                null,
                null
            );

            // When & Then
            mockMvc.perform(post("/api/v1/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

            verify(studentService, never()).createStudent(any(CreateStudentRequest.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when aadhaarNumber is invalid")
        void shouldReturnBadRequestWhenAadhaarInvalid() throws Exception {
            // Given
            CreateStudentRequest request = new CreateStudentRequest(
                "John",
                "Doe",
                LocalDate.of(2015, 5, 15),
                "1234567890",
                null,
                null,
                null,
                null,
                null,
                "12345"  // Invalid Aadhaar (not 12 digits)
            );

            // When & Then
            mockMvc.perform(post("/api/v1/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

            verify(studentService, never()).createStudent(any(CreateStudentRequest.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when dateOfBirth is in future")
        void shouldReturnBadRequestWhenDateOfBirthInFuture() throws Exception {
            // Given
            CreateStudentRequest request = new CreateStudentRequest(
                "John",
                "Doe",
                LocalDate.now().plusDays(1),  // Future date
                "1234567890",
                null,
                null,
                null,
                null,
                null,
                null
            );

            // When & Then
            mockMvc.perform(post("/api/v1/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

            verify(studentService, never()).createStudent(any(CreateStudentRequest.class));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/students/{studentId} - Get Student by ID")
    class GetStudentTests {

        @Test
        @DisplayName("Should retrieve student successfully by ID")
        void shouldRetrieveStudentSuccessfully() throws Exception {
            // Given
            String studentId = "STD-20241206-0001";
            StudentResponse expectedResponse = new StudentResponse(
                1L,
                studentId,
                "John",
                "Doe",
                LocalDate.of(2015, 5, 15),
                9,
                "1234567890",
                "john.doe@example.com",
                "123 Main St",
                "Robert Doe",
                "Jane Doe",
                "Mole on left arm",
                "123456789012",
                "ACTIVE",
                0L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "system",
                "system"
            );

            when(studentService.getStudent(studentId)).thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/students/{studentId}", studentId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.studentId").value(studentId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

            verify(studentService, times(1)).getStudent(studentId);
        }

        @Test
        @DisplayName("Should handle student not found scenario")
        void shouldHandleStudentNotFound() throws Exception {
            // Given
            String studentId = "STD-99999999-9999";
            when(studentService.getStudent(studentId))
                .thenThrow(new RuntimeException("Student not found"));

            // When & Then
            mockMvc.perform(get("/api/v1/students/{studentId}", studentId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

            verify(studentService, times(1)).getStudent(studentId);
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/students/{studentId} - Update Student")
    class UpdateStudentTests {

        @Test
        @DisplayName("Should update student successfully with valid request")
        void shouldUpdateStudentSuccessfully() throws Exception {
            // Given
            String studentId = "STD-20241206-0001";
            UpdateStudentRequest request = new UpdateStudentRequest(
                "Jane",
                "Smith",
                "9876543210",
                "ACTIVE",
                0L
            );

            StudentResponse expectedResponse = new StudentResponse(
                1L,
                studentId,
                "Jane",
                "Smith",
                LocalDate.of(2015, 5, 15),
                9,
                "9876543210",
                "john.doe@example.com",
                "123 Main St",
                "Robert Doe",
                "Jane Doe",
                "Mole on left arm",
                "123456789012",
                "ACTIVE",
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "system",
                "admin"
            );

            when(studentService.updateStudent(eq(studentId), any(UpdateStudentRequest.class)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/students/{studentId}", studentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.studentId").value(studentId))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.mobile").value("9876543210"))
                .andExpect(jsonPath("$.version").value(1));

            verify(studentService, times(1)).updateStudent(eq(studentId), any(UpdateStudentRequest.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when update request has invalid mobile")
        void shouldReturnBadRequestWhenUpdateHasInvalidMobile() throws Exception {
            // Given
            String studentId = "STD-20241206-0001";
            UpdateStudentRequest request = new UpdateStudentRequest(
                "Jane",
                "Smith",
                "123",  // Invalid mobile
                "ACTIVE",
                0L
            );

            // When & Then
            mockMvc.perform(put("/api/v1/students/{studentId}", studentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

            verify(studentService, never()).updateStudent(anyString(), any(UpdateStudentRequest.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/students/{studentId} - Delete Student")
    class DeleteStudentTests {

        @Test
        @DisplayName("Should delete student successfully")
        void shouldDeleteStudentSuccessfully() throws Exception {
            // Given
            String studentId = "STD-20241206-0001";
            doNothing().when(studentService).deleteStudent(studentId);

            // When & Then
            mockMvc.perform(delete("/api/v1/students/{studentId}", studentId))
                .andExpect(status().isNoContent());

            verify(studentService, times(1)).deleteStudent(studentId);
        }

        @Test
        @DisplayName("Should handle student not found during deletion")
        void shouldHandleStudentNotFoundDuringDeletion() throws Exception {
            // Given
            String studentId = "STD-99999999-9999";
            doThrow(new RuntimeException("Student not found"))
                .when(studentService).deleteStudent(studentId);

            // When & Then
            mockMvc.perform(delete("/api/v1/students/{studentId}", studentId))
                .andExpect(status().is5xxServerError());

            verify(studentService, times(1)).deleteStudent(studentId);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/students - Search Students")
    class SearchStudentsTests {

        @Test
        @DisplayName("Should search students with default pagination")
        void shouldSearchStudentsWithDefaultPagination() throws Exception {
            // Given
            List<StudentSummaryResponse> students = Arrays.asList(
                new StudentSummaryResponse(1L, "STD-20241206-0001", "John", "Doe", "1234567890", "ACTIVE", LocalDateTime.now()),
                new StudentSummaryResponse(2L, "STD-20241206-0002", "Jane", "Smith", "9876543210", "ACTIVE", LocalDateTime.now())
            );

            PagedStudentResponse.PageableInfo pageInfo = new PagedStudentResponse.PageableInfo(
                0, 20, 2L, 1, true, true
            );
            PagedStudentResponse expectedResponse = new PagedStudentResponse(students, pageInfo);

            when(studentService.searchStudents(isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/students")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].studentId").value("STD-20241206-0001"))
                .andExpect(jsonPath("$.content[1].studentId").value("STD-20241206-0002"))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(2))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(1))
                .andExpect(jsonPath("$.pageInfo.page").value(0))
                .andExpect(jsonPath("$.pageInfo.size").value(20));

            verify(studentService, times(1)).searchStudents(isNull(), isNull(), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should search students with lastName filter")
        void shouldSearchStudentsWithLastNameFilter() throws Exception {
            // Given
            String lastName = "Doe";
            List<StudentSummaryResponse> students = Arrays.asList(
                new StudentSummaryResponse(1L, "STD-20241206-0001", "John", "Doe", "1234567890", "ACTIVE", LocalDateTime.now())
            );

            PagedStudentResponse.PageableInfo pageInfo = new PagedStudentResponse.PageableInfo(
                0, 20, 1L, 1, true, true
            );
            PagedStudentResponse expectedResponse = new PagedStudentResponse(students, pageInfo);

            when(studentService.searchStudents(eq(lastName), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/students")
                    .param("lastName", lastName)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(1));

            verify(studentService, times(1)).searchStudents(eq(lastName), isNull(), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should search students with fathersName filter")
        void shouldSearchStudentsWithFathersNameFilter() throws Exception {
            // Given
            String fathersName = "Robert";
            List<StudentSummaryResponse> students = Arrays.asList(
                new StudentSummaryResponse(1L, "STD-20241206-0001", "John", "Doe", "1234567890", "ACTIVE", LocalDateTime.now())
            );

            PagedStudentResponse.PageableInfo pageInfo = new PagedStudentResponse.PageableInfo(
                0, 20, 1L, 1, true, true
            );
            PagedStudentResponse expectedResponse = new PagedStudentResponse(students, pageInfo);

            when(studentService.searchStudents(isNull(), eq(fathersName), isNull(), any(Pageable.class)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/students")
                    .param("fathersName", fathersName)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(1));

            verify(studentService, times(1)).searchStudents(isNull(), eq(fathersName), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should search students with status filter")
        void shouldSearchStudentsWithStatusFilter() throws Exception {
            // Given
            String status = "ACTIVE";
            List<StudentSummaryResponse> students = Arrays.asList(
                new StudentSummaryResponse(1L, "STD-20241206-0001", "John", "Doe", "1234567890", "ACTIVE", LocalDateTime.now()),
                new StudentSummaryResponse(2L, "STD-20241206-0002", "Jane", "Smith", "9876543210", "ACTIVE", LocalDateTime.now())
            );

            PagedStudentResponse.PageableInfo pageInfo = new PagedStudentResponse.PageableInfo(
                0, 20, 2L, 1, true, true
            );
            PagedStudentResponse expectedResponse = new PagedStudentResponse(students, pageInfo);

            when(studentService.searchStudents(isNull(), isNull(), eq(status), any(Pageable.class)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/students")
                    .param("status", status)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.content[1].status").value("ACTIVE"));

            verify(studentService, times(1)).searchStudents(isNull(), isNull(), eq(status), any(Pageable.class));
        }

        @Test
        @DisplayName("Should search students with custom pagination")
        void shouldSearchStudentsWithCustomPagination() throws Exception {
            // Given
            List<StudentSummaryResponse> students = Arrays.asList(
                new StudentSummaryResponse(1L, "STD-20241206-0001", "John", "Doe", "1234567890", "ACTIVE", LocalDateTime.now())
            );

            PagedStudentResponse.PageableInfo pageInfo = new PagedStudentResponse.PageableInfo(
                1, 5, 15L, 3, false, false
            );
            PagedStudentResponse expectedResponse = new PagedStudentResponse(students, pageInfo);

            when(studentService.searchStudents(isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/students")
                    .param("page", "1")
                    .param("size", "5")
                    .param("sort", "firstName")
                    .param("direction", "ASC")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo.totalElements").value(15))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(3))
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(5));

            verify(studentService, times(1)).searchStudents(isNull(), isNull(), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should search students with all filters combined")
        void shouldSearchStudentsWithAllFiltersCombined() throws Exception {
            // Given
            String lastName = "Doe";
            String fathersName = "Robert";
            String status = "ACTIVE";

            List<StudentSummaryResponse> students = Arrays.asList(
                new StudentSummaryResponse(1L, "STD-20241206-0001", "John", "Doe", "1234567890", "ACTIVE", LocalDateTime.now())
            );

            PagedStudentResponse.PageableInfo pageInfo = new PagedStudentResponse.PageableInfo(
                0, 20, 1L, 1, true, true
            );
            PagedStudentResponse expectedResponse = new PagedStudentResponse(students, pageInfo);

            when(studentService.searchStudents(eq(lastName), eq(fathersName), eq(status), any(Pageable.class)))
                .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/students")
                    .param("lastName", lastName)
                    .param("fathersName", fathersName)
                    .param("status", status)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(1));

            verify(studentService, times(1)).searchStudents(eq(lastName), eq(fathersName), eq(status), any(Pageable.class));
        }
    }
}
