package com.school.student.service;

import com.school.student.controller.dto.request.StudentRequest;
import com.school.student.controller.dto.request.StudentUpdateRequest;
import com.school.student.controller.dto.response.StudentResponse;
import com.school.student.domain.exception.DuplicateMobileException;
import com.school.student.domain.exception.StudentNotFoundException;
import com.school.student.domain.model.Student;
import com.school.student.domain.model.StudentStatus;
import com.school.student.domain.repository.StudentRepository;
import com.school.student.rules.RuleExecutor;
import com.school.student.rules.ValidationResult;
import com.school.student.service.mapper.StudentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentService
 * Uses Mockito to mock dependencies
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private RuleExecutor ruleExecutor;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("Should register student successfully")
    void shouldRegisterStudentSuccessfully() {
        // Given
        StudentRequest request = StudentRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2010, 5, 15))
            .mobile("9876543210")
            .build();

        Student student = Student.builder()
            .firstName("John")
            .lastName("Doe")
            .mobile("9876543210")
            .build();

        Student savedStudent = Student.builder()
            .id(1L)
            .studentId("STD-20260128-0001")
            .firstName("John")
            .lastName("Doe")
            .mobile("9876543210")
            .status(StudentStatus.ACTIVE)
            .build();

        StudentResponse expectedResponse = StudentResponse.builder()
            .studentId("STD-20260128-0001")
            .firstName("John")
            .lastName("Doe")
            .build();

        ValidationResult validationResult = new ValidationResult();

        when(studentRepository.existsByMobile(anyString())).thenReturn(false);
        when(studentMapper.toDomain(any())).thenReturn(student);
        when(ruleExecutor.validate(any())).thenReturn(validationResult);
        when(studentRepository.countByCreatedAtDate(any())).thenReturn(0L);
        when(studentRepository.save(any())).thenReturn(savedStudent);
        when(studentMapper.toResponse(any())).thenReturn(expectedResponse);

        // When
        StudentResponse response = studentService.registerStudent(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStudentId()).isEqualTo("STD-20260128-0001");
        verify(studentRepository).save(any(Student.class));
        verify(ruleExecutor).validate(any(Student.class));
    }

    @Test
    @DisplayName("Should throw exception when registering with duplicate mobile")
    void shouldThrowExceptionForDuplicateMobile() {
        // Given
        StudentRequest request = StudentRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .mobile("9876543210")
            .build();

        when(studentRepository.existsByMobile(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> studentService.registerStudent(request))
            .isInstanceOf(DuplicateMobileException.class);

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get student by ID successfully")
    void shouldGetStudentByIdSuccessfully() {
        // Given
        String studentId = "STD-20260128-0001";
        Student student = Student.builder()
            .studentId(studentId)
            .firstName("John")
            .lastName("Doe")
            .build();

        StudentResponse expectedResponse = StudentResponse.builder()
            .studentId(studentId)
            .firstName("John")
            .lastName("Doe")
            .build();

        when(studentRepository.findByStudentId(studentId)).thenReturn(Optional.of(student));
        when(studentMapper.toResponse(any())).thenReturn(expectedResponse);

        // When
        StudentResponse response = studentService.getStudentById(studentId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStudentId()).isEqualTo(studentId);
        verify(studentRepository).findByStudentId(studentId);
    }

    @Test
    @DisplayName("Should throw exception when student not found")
    void shouldThrowExceptionWhenStudentNotFound() {
        // Given
        String studentId = "STD-20260128-9999";
        when(studentRepository.findByStudentId(studentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.getStudentById(studentId))
            .isInstanceOf(StudentNotFoundException.class)
            .hasMessageContaining(studentId);
    }

    @Test
    @DisplayName("Should search students with filters")
    void shouldSearchStudentsWithFilters() {
        // Given
        String lastName = "Doe";
        String status = "ACTIVE";
        Pageable pageable = PageRequest.of(0, 10);

        Student student = Student.builder()
            .studentId("STD-20260128-0001")
            .lastName("Doe")
            .status(StudentStatus.ACTIVE)
            .build();

        Page<Student> studentPage = new PageImpl<>(List.of(student));
        StudentResponse response = StudentResponse.builder()
            .studentId("STD-20260128-0001")
            .build();

        when(studentRepository.searchStudents(lastName, status, pageable)).thenReturn(studentPage);
        when(studentMapper.toResponse(any())).thenReturn(response);

        // When
        Page<StudentResponse> result = studentService.searchStudents(lastName, status, pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(studentRepository).searchStudents(lastName, status, pageable);
    }

    @Test
    @DisplayName("Should update student successfully")
    void shouldUpdateStudentSuccessfully() {
        // Given
        String studentId = "STD-20260128-0001";
        StudentUpdateRequest request = StudentUpdateRequest.builder()
            .firstName("Jane")
            .lastName("Smith")
            .mobile("9876543211")
            .status(StudentStatus.ACTIVE)
            .version(1L)
            .build();

        Student existingStudent = Student.builder()
            .id(1L)
            .studentId(studentId)
            .firstName("John")
            .lastName("Doe")
            .mobile("9876543210")
            .status(StudentStatus.ACTIVE)
            .version(1L)
            .build();

        Student updatedStudent = Student.builder()
            .id(1L)
            .studentId(studentId)
            .firstName("Jane")
            .lastName("Smith")
            .mobile("9876543211")
            .status(StudentStatus.ACTIVE)
            .version(2L)
            .build();

        StudentResponse expectedResponse = StudentResponse.builder()
            .studentId(studentId)
            .firstName("Jane")
            .lastName("Smith")
            .build();

        when(studentRepository.findByStudentId(studentId)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any())).thenReturn(updatedStudent);
        when(studentMapper.toResponse(any())).thenReturn(expectedResponse);

        // When
        StudentResponse response = studentService.updateStudent(studentId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo("Jane");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should delete student successfully")
    void shouldDeleteStudentSuccessfully() {
        // Given
        String studentId = "STD-20260128-0001";
        Student student = Student.builder()
            .id(1L)
            .studentId(studentId)
            .build();

        when(studentRepository.findByStudentId(studentId)).thenReturn(Optional.of(student));

        // When
        studentService.deleteStudent(studentId);

        // Then
        verify(studentRepository).delete(student);
    }

    @Test
    @DisplayName("Should validate phone availability")
    void shouldValidatePhoneAvailability() {
        // Given
        String mobile = "9876543210";
        when(studentRepository.existsByMobile(mobile)).thenReturn(false);

        // When
        boolean available = studentService.validatePhone(mobile);

        // Then
        assertThat(available).isTrue();
        verify(studentRepository).existsByMobile(mobile);
    }
}
