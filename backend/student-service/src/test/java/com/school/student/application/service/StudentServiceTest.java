package com.school.student.application.service;

import com.school.student.application.mapper.StudentDTOMapper;
import com.school.student.common.exception.ValidationException;
import com.school.student.common.validation.ValidationResult;
import com.school.student.domain.model.Student;
import com.school.student.domain.model.StudentStatus;
import com.school.student.domain.model.valueobject.GuardianInfo;
import com.school.student.domain.model.valueobject.Mobile;
import com.school.student.domain.repository.StudentRepository;
import com.school.student.presentation.dto.StudentRequestDTO;
import com.school.student.presentation.dto.StudentResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentService.
 * Tests service orchestration logic with mocked dependencies.
 *
 * <p>Following TDD methodology - tests written before implementation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Unit Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private KieContainer kieContainer;

    @Mock
    private KieSession kieSession;

    @Mock
    private StudentDTOMapper studentDTOMapper;

    @Mock
    private StudentIdGenerator studentIdGenerator;

    @Mock
    private com.school.student.infrastructure.metrics.StudentMetrics studentMetrics;

    private StudentService studentService;

    // Test data
    private StudentRequestDTO validRequestDTO;
    private Student validStudent;
    private StudentResponseDTO validResponseDTO;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(
            studentRepository,
            kieContainer,
            studentDTOMapper,
            studentIdGenerator,
            studentMetrics
        );

        // Setup test data
        validRequestDTO = StudentRequestDTO.builder()
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

        validStudent = Student.register(
            "John",
            "Doe",
            LocalDate.of(2015, 5, 15),
            Mobile.of("9876543210"),
            GuardianInfo.of("James Doe", "Jane Doe")
        );
        validStudent.setId(1L);
        validStudent.setStudentId("STD-20260203-0001");
        validStudent.setEmail("john.doe@example.com");

        validResponseDTO = StudentResponseDTO.builder()
            .id(1L)
            .studentId("STD-20260203-0001")
            .firstName("John")
            .lastName("Doe")
            .status("ACTIVE")
            .build();

        // Mock KieContainer to return KieSession (lenient for tests that don't need it)
        lenient().when(kieContainer.newKieSession()).thenReturn(kieSession);
    }

    // ==================== Registration Tests ====================

    @Test
    @DisplayName("Should register student successfully with valid data")
    void shouldRegisterStudentSuccessfully() {
        // Arrange
        when(studentDTOMapper.toDomain(validRequestDTO)).thenReturn(validStudent);
        when(studentIdGenerator.generate()).thenReturn("STD-20260203-0001");
        when(studentRepository.save(any(Student.class))).thenReturn(validStudent);
        when(studentDTOMapper.toResponseDTO(validStudent)).thenReturn(validResponseDTO);

        // Act
        StudentResponseDTO result = studentService.registerStudent(validRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.studentId()).isEqualTo("STD-20260203-0001");

        // Verify Drools validation was executed
        verify(kieSession).insert(any(ValidationResult.class));
        verify(kieSession).insert(any(Student.class));
        verify(kieSession).setGlobal("studentRepository", studentRepository);
        verify(kieSession).fireAllRules();
        verify(kieSession).dispose();

        // Verify student was saved
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when Drools validation fails")
    void shouldThrowValidationExceptionWhenRulesFail() {
        // Arrange
        when(studentDTOMapper.toDomain(validRequestDTO)).thenReturn(validStudent);

        // Simulate Drools adding validation error
        doAnswer(invocation -> {
            ValidationResult result = invocation.getArgument(0);
            result.addError("dateOfBirth", "Age out of range", "AGE_OUT_OF_RANGE");
            return null;
        }).when(kieSession).insert(any(ValidationResult.class));

        // Act & Assert
        assertThatThrownBy(() -> studentService.registerStudent(validRequestDTO))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Validation failed");

        // Verify repository save was NOT called
        verify(studentRepository, never()).save(any(Student.class));
        verify(kieSession).dispose();
    }

    @Test
    @DisplayName("Should set student ID before saving")
    void shouldSetStudentIdBeforeSaving() {
        // Arrange
        when(studentDTOMapper.toDomain(validRequestDTO)).thenReturn(validStudent);
        when(studentIdGenerator.generate()).thenReturn("STD-20260203-0001");
        when(studentRepository.save(any(Student.class))).thenReturn(validStudent);
        when(studentDTOMapper.toResponseDTO(validStudent)).thenReturn(validResponseDTO);

        // Act
        studentService.registerStudent(validRequestDTO);

        // Assert
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentCaptor.capture());

        Student savedStudent = studentCaptor.getValue();
        assertThat(savedStudent.getStudentId()).isEqualTo("STD-20260203-0001");
    }

    @Test
    @DisplayName("Should dispose KieSession even when exception occurs")
    void shouldDisposeKieSessionOnException() {
        // Arrange
        when(studentDTOMapper.toDomain(validRequestDTO)).thenReturn(validStudent);
        when(studentRepository.save(any(Student.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> studentService.registerStudent(validRequestDTO))
            .isInstanceOf(RuntimeException.class);

        // Verify session was disposed
        verify(kieSession).dispose();
    }

    // ==================== Retrieval Tests ====================

    @Test
    @DisplayName("Should get student by ID successfully")
    void shouldGetStudentByIdSuccessfully() {
        // Arrange
        String studentId = "STD-20260203-0001";
        when(studentRepository.findByStudentId(studentId)).thenReturn(Optional.of(validStudent));
        when(studentDTOMapper.toResponseDTO(validStudent)).thenReturn(validResponseDTO);

        // Act
        StudentResponseDTO result = studentService.getStudentById(studentId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.studentId()).isEqualTo(studentId);
    }

    @Test
    @DisplayName("Should throw exception when student not found")
    void shouldThrowExceptionWhenStudentNotFound() {
        // Arrange
        String studentId = "STD-20260203-9999";
        when(studentRepository.findByStudentId(studentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.getStudentById(studentId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("not found");
    }

    // ==================== Deletion Tests ====================

    @Test
    @DisplayName("Should delete student successfully")
    void shouldDeleteStudentSuccessfully() {
        // Arrange
        String studentId = "STD-20260203-0001";
        when(studentRepository.findByStudentId(studentId)).thenReturn(Optional.of(validStudent));

        // Act
        studentService.deleteStudent(studentId);

        // Assert
        verify(studentRepository).deleteById(1L);
    }
}
