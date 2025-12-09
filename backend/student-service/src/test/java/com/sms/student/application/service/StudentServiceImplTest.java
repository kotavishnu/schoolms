package com.sms.student.application.service;

import com.sms.shared.exception.DuplicateResourceException;
import com.sms.shared.exception.ResourceNotFoundException;
import com.sms.shared.exception.ValidationException;
import com.sms.student.application.mapper.StudentMapper;
import com.sms.student.domain.model.Student;
import com.sms.student.domain.model.StudentStatus;
import com.sms.student.infrastructure.persistence.entity.StudentEntity;
import com.sms.student.infrastructure.persistence.repository.StudentRepository;
import com.sms.student.presentation.dto.CreateStudentRequest;
import com.sms.student.presentation.dto.StudentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Tests")
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentServiceImpl studentService;

    private CreateStudentRequest validRequest;
    private StudentEntity studentEntity;
    private Student studentDomain;
    private StudentDTO studentDTO;

    @BeforeEach
    void setUp() {
        // Prepare test data
        validRequest = new CreateStudentRequest(
            "Rahul",
            "Sharma",
            LocalDate.of(2012, 5, 15), // Age 12 (valid)
            "+919876543210",
            "rahul@example.com",
            "123 Main Street",
            "Raj Sharma",
            "Priya Sharma",
            "Mole on left cheek",
            "123456789012"
        );

        studentEntity = StudentEntity.builder()
            .studentId(1L)
            .studentKey("STU-2025-0001")
            .firstName("Rahul")
            .lastName("Sharma")
            .dateOfBirth(LocalDate.of(2012, 5, 15))
            .mobile("+919876543210")
            .email("rahul@example.com")
            .status(StudentStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        studentDomain = Student.builder()
            .studentId(1L)
            .studentKey("STU-2025-0001")
            .firstName("Rahul")
            .lastName("Sharma")
            .dateOfBirth(LocalDate.of(2012, 5, 15))
            .mobile("+919876543210")
            .email("rahul@example.com")
            .status(StudentStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        studentDTO = new StudentDTO(
            1L,
            "STU-2025-0001",
            "Rahul",
            "Sharma",
            LocalDate.of(2012, 5, 15),
            12,
            "+919876543210",
            "rahul@example.com",
            "123 Main Street",
            "Raj Sharma",
            "Priya Sharma",
            "Mole on left cheek",
            "123456789012",
            StudentStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Should create student successfully with valid data")
    void testCreateStudent_Success() {
        // Arrange
        when(studentRepository.existsByMobile(validRequest.mobile())).thenReturn(false);
        when(studentRepository.findMaxSequenceForYear("STU-2025-")).thenReturn(null);
        when(studentMapper.requestToDomain(validRequest)).thenReturn(studentDomain);
        when(studentMapper.domainToEntity(any(Student.class))).thenReturn(studentEntity);
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(studentEntity);
        when(studentMapper.entityToDTO(studentEntity)).thenReturn(studentDTO);

        // Act
        StudentDTO result = studentService.createStudent(validRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.studentKey()).isEqualTo("STU-2025-0001");
        assertThat(result.firstName()).isEqualTo("Rahul");
        verify(studentRepository, times(1)).save(any(StudentEntity.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when age is below minimum")
    void testCreateStudent_AgeBelow3_ThrowsException() {
        // Arrange
        CreateStudentRequest invalidAgeRequest = new CreateStudentRequest(
            "Child",
            "Too Young",
            LocalDate.of(2023, 1, 1), // Age 1 (invalid)
            "+919876543211",
            "child@example.com",
            "123 Main Street",
            "Guardian Name",
            "Mother Name",
            "Mark",
            "123456789013"
        );

        // Act & Assert
        assertThatThrownBy(() -> studentService.createStudent(invalidAgeRequest))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("age must be between 3 and 18");

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when mobile already exists")
    void testCreateStudent_DuplicateMobile_ThrowsException() {
        // Arrange
        when(studentRepository.existsByMobile(validRequest.mobile())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> studentService.createStudent(validRequest))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("already exists");

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get student by key successfully")
    void testGetStudent_Success() {
        // Arrange
        when(studentRepository.findByStudentKey("STU-2025-0001"))
            .thenReturn(Optional.of(studentEntity));
        when(studentMapper.entityToDTO(studentEntity)).thenReturn(studentDTO);

        // Act
        StudentDTO result = studentService.getStudent("STU-2025-0001");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.studentKey()).isEqualTo("STU-2025-0001");
        verify(studentRepository, times(1)).findByStudentKey("STU-2025-0001");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when student not found")
    void testGetStudent_NotFound_ThrowsException() {
        // Arrange
        when(studentRepository.findByStudentKey("INVALID-KEY"))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.getStudent("INVALID-KEY"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should update student successfully")
    void testUpdateStudent_Success() {
        // Arrange
        CreateStudentRequest updateRequest = new CreateStudentRequest(
            "Rahul Updated",
            "Sharma Updated",
            LocalDate.of(2012, 5, 15),
            "+919876543210",
            "updated@example.com",
            "Updated Address",
            "Updated Guardian",
            "Updated Mother",
            "Updated Mark",
            "123456789012"
        );

        // Use same mobile as existing to avoid duplicate check
        StudentEntity updatedEntity = StudentEntity.builder()
            .studentId(1L)
            .studentKey("STU-2025-0001")
            .firstName("Rahul Updated")
            .lastName("Sharma Updated")
            .dateOfBirth(LocalDate.of(2012, 5, 15))
            .mobile("+919876543210")
            .email("updated@example.com")
            .status(StudentStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(studentRepository.findByStudentKey("STU-2025-0001"))
            .thenReturn(Optional.of(studentEntity));
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(updatedEntity);
        when(studentMapper.entityToDTO(updatedEntity)).thenReturn(studentDTO);

        // Act
        StudentDTO result = studentService.updateStudent("STU-2025-0001", updateRequest);

        // Assert
        assertThat(result).isNotNull();
        verify(studentRepository, times(1)).save(any(StudentEntity.class));
    }

    @Test
    @DisplayName("Should delete student successfully")
    void testDeleteStudent_Success() {
        // Arrange
        when(studentRepository.findByStudentKey("STU-2025-0001"))
            .thenReturn(Optional.of(studentEntity));

        // Act
        studentService.deleteStudent("STU-2025-0001");

        // Assert
        verify(studentRepository, times(1)).delete(studentEntity);
    }

    @Test
    @DisplayName("Should search students successfully")
    void testSearchStudents_Success() {
        // Arrange
        Page<StudentEntity> studentPage = new PageImpl<>(List.of(studentEntity));
        when(studentRepository.searchByLastNameOrGuardian("Sharma", "Sharma", PageRequest.of(0, 20)))
            .thenReturn(studentPage);
        when(studentMapper.entityToDTO(studentEntity)).thenReturn(studentDTO);

        // Act
        Page<StudentDTO> result = studentService.searchStudents("Sharma", 0, 20);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).lastName()).isEqualTo("Sharma");
    }
}
