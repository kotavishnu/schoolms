package com.school.management.service;

import com.school.management.dto.request.StudentRequestDTO;
import com.school.management.dto.response.StudentResponseDTO;
import com.school.management.exception.DuplicateResourceException;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.exception.ValidationException;
import com.school.management.mapper.StudentMapper;
import com.school.management.model.SchoolClass;
import com.school.management.model.Student;
import com.school.management.model.StudentStatus;
import com.school.management.repository.ClassRepository;
import com.school.management.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentService
 *
 * Uses Mockito to mock dependencies and test business logic.
 *
 * @author School Management Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    private StudentRequestDTO requestDTO;
    private Student student;
    private SchoolClass schoolClass;
    private StudentResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        requestDTO = StudentRequestDTO.builder()
                .firstName("Rajesh")
                .lastName("Kumar")
                .dateOfBirth(LocalDate.of(2012, 5, 15))
                .address("123 MG Road, Bangalore")
                .mobile("9876543210")
                .motherName("Priya Kumar")
                .fatherName("Suresh Kumar")
                .classId(1L)
                .build();

        schoolClass = SchoolClass.builder()
                .id(1L)
                .classNumber(5)
                .section("A")
                .academicYear("2024-2025")
                .capacity(50)
                .currentEnrollment(20)
                .build();

        student = Student.builder()
                .id(1L)
                .firstName("Rajesh")
                .lastName("Kumar")
                .dateOfBirth(LocalDate.of(2012, 5, 15))
                .mobile("9876543210")
                .schoolClass(schoolClass)
                .status(StudentStatus.ACTIVE)
                .enrollmentDate(LocalDate.now())
                .build();

        responseDTO = StudentResponseDTO.builder()
                .id(1L)
                .firstName("Rajesh")
                .lastName("Kumar")
                .fullName("Rajesh Kumar")
                .mobile("9876543210")
                .classId(1L)
                .className("Class 5 - A")
                .status(StudentStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should create student successfully")
    void shouldCreateStudentSuccessfully() {
        // Given
        when(studentRepository.existsByMobile(anyString())).thenReturn(false);
        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(studentRepository.countBySchoolClassId(1L)).thenReturn(20L);
        when(studentMapper.toEntity(any(StudentRequestDTO.class))).thenReturn(student);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentMapper.toResponseDTO(any(Student.class))).thenReturn(responseDTO);

        // When
        StudentResponseDTO result = studentService.createStudent(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Rajesh");
        assertThat(result.getLastName()).isEqualTo("Kumar");

        verify(studentRepository).existsByMobile("9876543210");
        verify(classRepository).findById(1L);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw exception when mobile number already exists")
    void shouldThrowExceptionWhenMobileExists() {
        // Given
        when(studentRepository.existsByMobile("9876543210")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> studentService.createStudent(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("mobile");

        verify(studentRepository).existsByMobile("9876543210");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw exception when class not found")
    void shouldThrowExceptionWhenClassNotFound() {
        // Given
        when(studentRepository.existsByMobile(anyString())).thenReturn(false);
        when(classRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.createStudent(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("SchoolClass");

        verify(classRepository).findById(1L);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw exception when class capacity exceeded")
    void shouldThrowExceptionWhenClassFull() {
        // Given
        when(studentRepository.existsByMobile(anyString())).thenReturn(false);
        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(studentRepository.countBySchoolClassId(1L)).thenReturn(50L);

        // When & Then
        assertThatThrownBy(() -> studentService.createStudent(requestDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("capacity exceeded");

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should get student by ID successfully")
    void shouldGetStudentByIdSuccessfully() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.toResponseDTO(any(Student.class))).thenReturn(responseDTO);

        // When
        StudentResponseDTO result = studentService.getStudent(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Rajesh");

        verify(studentRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when student not found")
    void shouldThrowExceptionWhenStudentNotFound() {
        // Given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.getStudent(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student")
                .hasMessageContaining("999");

        verify(studentRepository).findById(999L);
    }

    @Test
    @DisplayName("Should delete student successfully")
    void shouldDeleteStudentSuccessfully() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // When
        studentService.deleteStudent(1L);

        // Then
        verify(studentRepository).findById(1L);
        verify(studentRepository).deleteById(1L);
        verify(classRepository).save(any(SchoolClass.class));
    }
}
