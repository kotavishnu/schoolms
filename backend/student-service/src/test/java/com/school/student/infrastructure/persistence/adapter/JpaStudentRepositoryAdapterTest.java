package com.school.student.infrastructure.persistence.adapter;

import com.school.student.domain.model.Student;
import com.school.student.domain.model.StudentStatus;
import com.school.student.domain.model.valueobject.GuardianInfo;
import com.school.student.domain.model.valueobject.Mobile;
import com.school.student.infrastructure.persistence.entity.StudentEntity;
import com.school.student.infrastructure.persistence.mapper.StudentEntityMapper;
import com.school.student.infrastructure.persistence.repository.JpaStudentRepositoryInterface;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JpaStudentRepositoryAdapter.
 * Tests repository adapter with mocked JPA repository.
 *
 * <p>Following D-013: Infrastructure Layer Testing - Test adapters with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JpaStudentRepositoryAdapter Unit Tests")
class JpaStudentRepositoryAdapterTest {

    @Mock
    private JpaStudentRepositoryInterface jpaRepository;

    @Mock
    private StudentEntityMapper mapper;

    @InjectMocks
    private JpaStudentRepositoryAdapter adapter;

    // Test data
    private Student testStudent;
    private StudentEntity testEntity;

    @BeforeEach
    void setUp() {
        testStudent = Student.register(
            "John",
            "Doe",
            LocalDate.of(2015, 5, 15),
            Mobile.of("9876543210"),
            GuardianInfo.of("James Doe", "Jane Doe")
        );
        testStudent.setId(1L);
        testStudent.setStudentId("STD-20260204-0001");

        testEntity = StudentEntity.builder()
            .id(1L)
            .studentId("STD-20260204-0001")
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .status(StudentEntity.StudentStatusEnum.ACTIVE)
            .version(0L)
            .build();
    }

    // ==================== Save Tests ====================

    @Test
    @DisplayName("Should save student successfully")
    void shouldSaveStudentSuccessfully() {
        // Arrange
        when(mapper.toEntity(testStudent)).thenReturn(testEntity);
        when(jpaRepository.save(testEntity)).thenReturn(testEntity);
        when(mapper.toDomain(testEntity)).thenReturn(testStudent);

        // Act
        Student result = adapter.save(testStudent);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStudentId()).isEqualTo("STD-20260204-0001");
        verify(mapper).toEntity(testStudent);
        verify(jpaRepository).save(testEntity);
        verify(mapper).toDomain(testEntity);
    }

    @Test
    @DisplayName("Should throw exception when saving null student")
    void shouldThrowExceptionWhenSavingNullStudent() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.save(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Student cannot be null");

        verify(jpaRepository, never()).save(any());
    }

    // ==================== Find By StudentId Tests ====================

    @Test
    @DisplayName("Should find student by studentId successfully")
    void shouldFindStudentByStudentIdSuccessfully() {
        // Arrange
        String studentId = "STD-20260204-0001";
        when(jpaRepository.findByStudentIdWithEnrollments(studentId)).thenReturn(Optional.of(testEntity));
        when(mapper.toDomain(testEntity)).thenReturn(testStudent);

        // Act
        Optional<Student> result = adapter.findByStudentId(studentId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getStudentId()).isEqualTo(studentId);
        verify(jpaRepository).findByStudentIdWithEnrollments(studentId);
        verify(mapper).toDomain(testEntity);
    }

    @Test
    @DisplayName("Should return empty when student not found by studentId")
    void shouldReturnEmptyWhenStudentNotFoundByStudentId() {
        // Arrange
        String studentId = "STD-20260204-9999";
        when(jpaRepository.findByStudentIdWithEnrollments(studentId)).thenReturn(Optional.empty());

        // Act
        Optional<Student> result = adapter.findByStudentId(studentId);

        // Assert
        assertThat(result).isEmpty();
        verify(jpaRepository).findByStudentIdWithEnrollments(studentId);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should throw exception when studentId is null")
    void shouldThrowExceptionWhenStudentIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.findByStudentId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Student ID cannot be null or blank");

        verify(jpaRepository, never()).findByStudentIdWithEnrollments(any());
    }

    @Test
    @DisplayName("Should throw exception when studentId is blank")
    void shouldThrowExceptionWhenStudentIdIsBlank() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.findByStudentId("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Student ID cannot be null or blank");

        verify(jpaRepository, never()).findByStudentIdWithEnrollments(any());
    }

    // ==================== Find By Id Tests ====================

    @Test
    @DisplayName("Should find student by id successfully")
    void shouldFindStudentByIdSuccessfully() {
        // Arrange
        Long id = 1L;
        when(jpaRepository.findById(id)).thenReturn(Optional.of(testEntity));
        when(mapper.toDomain(testEntity)).thenReturn(testStudent);

        // Act
        Optional<Student> result = adapter.findById(id);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        verify(jpaRepository).findById(id);
    }

    @Test
    @DisplayName("Should throw exception when id is null")
    void shouldThrowExceptionWhenIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.findById(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ID cannot be null");

        verify(jpaRepository, never()).findById(any());
    }

    // ==================== Find By LastName Tests ====================

    @Test
    @DisplayName("Should find students by lastName containing")
    void shouldFindStudentsByLastNameContaining() {
        // Arrange
        String lastName = "Doe";
        Pageable pageable = PageRequest.of(0, 10);
        Page<StudentEntity> entityPage = new PageImpl<>(List.of(testEntity));

        when(jpaRepository.findByLastNameContaining(lastName, pageable)).thenReturn(entityPage);
        when(mapper.toDomain(testEntity)).thenReturn(testStudent);

        // Act
        Page<Student> result = adapter.findByLastNameContaining(lastName, pageable);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLastName()).isEqualTo("Doe");
        verify(jpaRepository).findByLastNameContaining(lastName, pageable);
    }

    @Test
    @DisplayName("Should throw exception when lastName is null in search")
    void shouldThrowExceptionWhenLastNameIsNullInSearch() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        assertThatThrownBy(() -> adapter.findByLastNameContaining(null, pageable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Last name cannot be null");

        verify(jpaRepository, never()).findByLastNameContaining(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when pageable is null in lastName search")
    void shouldThrowExceptionWhenPageableIsNullInLastNameSearch() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.findByLastNameContaining("Doe", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Pageable cannot be null");

        verify(jpaRepository, never()).findByLastNameContaining(any(), any());
    }

    // ==================== Find By Status Tests ====================

    @Test
    @DisplayName("Should find students by status")
    void shouldFindStudentsByStatus() {
        // Arrange
        StudentStatus status = StudentStatus.ACTIVE;
        Pageable pageable = PageRequest.of(0, 10);
        Page<StudentEntity> entityPage = new PageImpl<>(List.of(testEntity));

        when(jpaRepository.findByStatus(StudentEntity.StudentStatusEnum.ACTIVE, pageable)).thenReturn(entityPage);
        when(mapper.toDomain(testEntity)).thenReturn(testStudent);

        // Act
        Page<Student> result = adapter.findByStatus(status, pageable);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        verify(jpaRepository).findByStatus(StudentEntity.StudentStatusEnum.ACTIVE, pageable);
    }

    @Test
    @DisplayName("Should throw exception when status is null in findByStatus")
    void shouldThrowExceptionWhenStatusIsNullInFindByStatus() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        assertThatThrownBy(() -> adapter.findByStatus(null, pageable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Status cannot be null");

        verify(jpaRepository, never()).findByStatus(any(), any());
    }

    // ==================== Find By LastName And Status Tests ====================

    @Test
    @DisplayName("Should find students by lastName and status")
    void shouldFindStudentsByLastNameAndStatus() {
        // Arrange
        String lastName = "Doe";
        StudentStatus status = StudentStatus.ACTIVE;
        Pageable pageable = PageRequest.of(0, 10);
        Page<StudentEntity> entityPage = new PageImpl<>(List.of(testEntity));

        when(jpaRepository.findByLastNameContainingAndStatus(lastName, StudentEntity.StudentStatusEnum.ACTIVE, pageable))
            .thenReturn(entityPage);
        when(mapper.toDomain(testEntity)).thenReturn(testStudent);

        // Act
        Page<Student> result = adapter.findByLastNameContainingAndStatus(lastName, status, pageable);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        verify(jpaRepository).findByLastNameContainingAndStatus(lastName, StudentEntity.StudentStatusEnum.ACTIVE, pageable);
    }

    // ==================== Mobile Existence Tests ====================

    @Test
    @DisplayName("Should check if mobile exists")
    void shouldCheckIfMobileExists() {
        // Arrange
        Mobile mobile = Mobile.of("9876543210");
        when(jpaRepository.existsByMobile("9876543210")).thenReturn(true);

        // Act
        boolean result = adapter.existsByMobile(mobile);

        // Assert
        assertThat(result).isTrue();
        verify(jpaRepository).existsByMobile("9876543210");
    }

    @Test
    @DisplayName("Should throw exception when mobile is null in existsByMobile")
    void shouldThrowExceptionWhenMobileIsNullInExistsByMobile() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.existsByMobile(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Mobile cannot be null");

        verify(jpaRepository, never()).existsByMobile(any());
    }

    @Test
    @DisplayName("Should check if mobile exists for other students")
    void shouldCheckIfMobileExistsForOtherStudents() {
        // Arrange
        Mobile mobile = Mobile.of("9876543210");
        Long id = 1L;
        when(jpaRepository.existsByMobileAndIdNot("9876543210", id)).thenReturn(false);

        // Act
        boolean result = adapter.existsByMobileAndIdNot(mobile, id);

        // Assert
        assertThat(result).isFalse();
        verify(jpaRepository).existsByMobileAndIdNot("9876543210", id);
    }

    @Test
    @DisplayName("Should throw exception when mobile is null in existsByMobileAndIdNot")
    void shouldThrowExceptionWhenMobileIsNullInExistsByMobileAndIdNot() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.existsByMobileAndIdNot(null, 1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Mobile cannot be null");

        verify(jpaRepository, never()).existsByMobileAndIdNot(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when id is null in existsByMobileAndIdNot")
    void shouldThrowExceptionWhenIdIsNullInExistsByMobileAndIdNot() {
        // Arrange
        Mobile mobile = Mobile.of("9876543210");

        // Act & Assert
        assertThatThrownBy(() -> adapter.existsByMobileAndIdNot(mobile, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ID cannot be null");

        verify(jpaRepository, never()).existsByMobileAndIdNot(any(), any());
    }

    // ==================== Delete Tests ====================

    @Test
    @DisplayName("Should delete student by id successfully")
    void shouldDeleteStudentByIdSuccessfully() {
        // Arrange
        Long id = 1L;
        doNothing().when(jpaRepository).deleteById(id);

        // Act
        adapter.deleteById(id);

        // Assert
        verify(jpaRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when deleting with null id")
    void shouldThrowExceptionWhenDeletingWithNullId() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.deleteById(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ID cannot be null");

        verify(jpaRepository, never()).deleteById(any());
    }

    // ==================== Count Tests ====================

    @Test
    @DisplayName("Should count all students")
    void shouldCountAllStudents() {
        // Arrange
        when(jpaRepository.count()).thenReturn(10L);

        // Act
        long result = adapter.count();

        // Assert
        assertThat(result).isEqualTo(10L);
        verify(jpaRepository).count();
    }

    @Test
    @DisplayName("Should count students by status")
    void shouldCountStudentsByStatus() {
        // Arrange
        StudentStatus status = StudentStatus.ACTIVE;
        when(jpaRepository.countByStatus(StudentEntity.StudentStatusEnum.ACTIVE)).thenReturn(5L);

        // Act
        long result = adapter.countByStatus(status);

        // Assert
        assertThat(result).isEqualTo(5L);
        verify(jpaRepository).countByStatus(StudentEntity.StudentStatusEnum.ACTIVE);
    }

    @Test
    @DisplayName("Should throw exception when status is null in countByStatus")
    void shouldThrowExceptionWhenStatusIsNullInCountByStatus() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.countByStatus(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Status cannot be null");

        verify(jpaRepository, never()).countByStatus(any());
    }

    // ==================== Find All Tests ====================

    @Test
    @DisplayName("Should find all students with pagination")
    void shouldFindAllStudentsWithPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<StudentEntity> entityPage = new PageImpl<>(List.of(testEntity));

        when(jpaRepository.findAll(pageable)).thenReturn(entityPage);
        when(mapper.toDomain(testEntity)).thenReturn(testStudent);

        // Act
        Page<Student> result = adapter.findAll(pageable);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        verify(jpaRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should throw exception when pageable is null in findAll")
    void shouldThrowExceptionWhenPageableIsNullInFindAll() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.findAll(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Pageable cannot be null");

        verify(jpaRepository, never()).findAll(any(Pageable.class));
    }
}
