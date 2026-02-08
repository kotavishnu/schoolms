package com.school.student.infrastructure.persistence.adapter;

import com.school.student.domain.model.Enrollment;
import com.school.student.domain.model.EnrollmentStatus;
import com.school.student.infrastructure.persistence.entity.EnrollmentEntity;
import com.school.student.infrastructure.persistence.entity.StudentEntity;
import com.school.student.infrastructure.persistence.mapper.EnrollmentEntityMapper;
import com.school.student.infrastructure.persistence.repository.JpaEnrollmentRepositoryInterface;
import com.school.student.infrastructure.persistence.repository.JpaStudentRepositoryInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JpaEnrollmentRepositoryAdapter.
 * Tests enrollment repository adapter with mocked JPA repository.
 *
 * <p>Following D-013: Infrastructure Layer Testing.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JpaEnrollmentRepositoryAdapter Unit Tests")
class JpaEnrollmentRepositoryAdapterTest {

    @Mock
    private JpaEnrollmentRepositoryInterface jpaRepository;

    @Mock
    private JpaStudentRepositoryInterface studentJpaRepository;

    @Mock
    private EnrollmentEntityMapper mapper;

    @InjectMocks
    private JpaEnrollmentRepositoryAdapter adapter;

    // Test data
    private Enrollment testEnrollment;
    private EnrollmentEntity testEntity;
    private StudentEntity testStudent;

    @BeforeEach
    void setUp() {
        testEnrollment = Enrollment.enroll(
            1L,
            "2025-2026",
            "Grade 5",
            "Section A",
            LocalDate.of(2025, 4, 1),
            null
        );
        testEnrollment.setId(1L);

        testEntity = EnrollmentEntity.builder()
            .id(1L)
            .academicYear("2025-2026")
            .gradeClass("Grade 5")
            .section("Section A")
            .enrollmentDate(LocalDate.of(2025, 4, 1))
            .status(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE)
            .build();

        testStudent = StudentEntity.builder()
            .id(1L)
            .studentId("STD-20260204-0001")
            .firstName("John")
            .lastName("Doe")
            .build();
    }

    // ==================== Save Tests ====================

    @Test
    @DisplayName("Should save enrollment successfully")
    void shouldSaveEnrollmentSuccessfully() {
        // Arrange
        when(mapper.toEntity(testEnrollment)).thenReturn(testEntity);
        when(studentJpaRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(jpaRepository.save(testEntity)).thenReturn(testEntity);
        when(mapper.toDomain(testEntity)).thenReturn(testEnrollment);

        // Act
        Enrollment result = adapter.save(testEnrollment);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAcademicYear()).isEqualTo("2025-2026");
        verify(mapper).toEntity(testEnrollment);
        verify(studentJpaRepository).findById(1L);
        // Note: Cannot verify setStudent on entity as it's not a mock
        verify(jpaRepository).save(any(EnrollmentEntity.class));
        verify(mapper).toDomain(testEntity);
    }

    @Test
    @DisplayName("Should throw exception when saving null enrollment")
    void shouldThrowExceptionWhenSavingNullEnrollment() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.save(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Enrollment cannot be null");

        verify(jpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when student not found during save")
    void shouldThrowExceptionWhenStudentNotFoundDuringSave() {
        // Arrange
        when(mapper.toEntity(testEnrollment)).thenReturn(testEntity);
        when(studentJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> adapter.save(testEnrollment))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Student not found with ID: 1");

        verify(jpaRepository, never()).save(any());
    }

    // ==================== Find By Id Tests ====================

    @Test
    @DisplayName("Should find enrollment by id successfully")
    void shouldFindEnrollmentByIdSuccessfully() {
        // Arrange
        Long id = 1L;
        when(jpaRepository.findById(id)).thenReturn(Optional.of(testEntity));
        when(mapper.toDomain(testEntity)).thenReturn(testEnrollment);

        // Act
        Optional<Enrollment> result = adapter.findById(id);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        verify(jpaRepository).findById(id);
        verify(mapper).toDomain(testEntity);
    }

    @Test
    @DisplayName("Should return empty when enrollment not found by id")
    void shouldReturnEmptyWhenEnrollmentNotFoundById() {
        // Arrange
        Long id = 999L;
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Enrollment> result = adapter.findById(id);

        // Assert
        assertThat(result).isEmpty();
        verify(jpaRepository).findById(id);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should throw exception when id is null in findById")
    void shouldThrowExceptionWhenIdIsNullInFindById() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.findById(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ID cannot be null");

        verify(jpaRepository, never()).findById(any());
    }

    // ==================== Find By StudentId Tests ====================

    @Test
    @DisplayName("Should find enrollments by student id")
    void shouldFindEnrollmentsByStudentId() {
        // Arrange
        Long studentId = 1L;
        when(jpaRepository.findByStudentId(studentId)).thenReturn(List.of(testEntity));
        when(mapper.toDomain(testEntity)).thenReturn(testEnrollment);

        // Act
        List<Enrollment> result = adapter.findByStudentId(studentId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStudentId()).isEqualTo(studentId);
        verify(jpaRepository).findByStudentId(studentId);
    }

    @Test
    @DisplayName("Should throw exception when student id is null in findByStudentId")
    void shouldThrowExceptionWhenStudentIdIsNullInFindByStudentId() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.findByStudentId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Student ID cannot be null");

        verify(jpaRepository, never()).findByStudentId(any());
    }

    // ==================== Find By StudentId And AcademicYear Tests ====================

    @Test
    @DisplayName("Should find enrollment by student id and academic year")
    void shouldFindEnrollmentByStudentIdAndAcademicYear() {
        // Arrange
        Long studentId = 1L;
        String academicYear = "2025-2026";
        when(jpaRepository.findByStudentIdAndAcademicYear(studentId, academicYear))
            .thenReturn(Optional.of(testEntity));
        when(mapper.toDomain(testEntity)).thenReturn(testEnrollment);

        // Act
        Optional<Enrollment> result = adapter.findByStudentIdAndAcademicYear(studentId, academicYear);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getAcademicYear()).isEqualTo(academicYear);
        verify(jpaRepository).findByStudentIdAndAcademicYear(studentId, academicYear);
    }

    @Test
    @DisplayName("Should throw exception when student id is null in findByStudentIdAndAcademicYear")
    void shouldThrowExceptionWhenStudentIdIsNullInFindByStudentIdAndAcademicYear() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.findByStudentIdAndAcademicYear(null, "2025-2026"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Student ID cannot be null");

        verify(jpaRepository, never()).findByStudentIdAndAcademicYear(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when academic year is null in findByStudentIdAndAcademicYear")
    void shouldThrowExceptionWhenAcademicYearIsNullInFindByStudentIdAndAcademicYear() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.findByStudentIdAndAcademicYear(1L, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Academic year cannot be null");

        verify(jpaRepository, never()).findByStudentIdAndAcademicYear(any(), any());
    }

    // ==================== Exists By StudentId And AcademicYear Tests ====================

    @Test
    @DisplayName("Should check if enrollment exists by student id and academic year")
    void shouldCheckIfEnrollmentExists() {
        // Arrange
        Long studentId = 1L;
        String academicYear = "2025-2026";
        when(jpaRepository.existsByStudentIdAndAcademicYear(studentId, academicYear)).thenReturn(true);

        // Act
        boolean result = adapter.existsByStudentIdAndAcademicYear(studentId, academicYear);

        // Assert
        assertThat(result).isTrue();
        verify(jpaRepository).existsByStudentIdAndAcademicYear(studentId, academicYear);
    }

    // ==================== Find Active Enrollments Tests ====================

    @Test
    @DisplayName("Should find active enrollments by student id")
    void shouldFindActiveEnrollmentsByStudentId() {
        // Arrange
        Long studentId = 1L;
        when(jpaRepository.findActiveEnrollmentsByStudentId(studentId)).thenReturn(List.of(testEntity));
        when(mapper.toDomain(testEntity)).thenReturn(testEnrollment);

        // Act
        List<Enrollment> result = adapter.findActiveEnrollmentsByStudentId(studentId);

        // Assert
        assertThat(result).hasSize(1);
        verify(jpaRepository).findActiveEnrollmentsByStudentId(studentId);
    }

    @Test
    @DisplayName("Should throw exception when student id is null in findActiveEnrollments")
    void shouldThrowExceptionWhenStudentIdIsNullInFindActiveEnrollments() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.findActiveEnrollmentsByStudentId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Student ID cannot be null");

        verify(jpaRepository, never()).findActiveEnrollmentsByStudentId(any());
    }

    // ==================== Find By AcademicYear Tests ====================

    @Test
    @DisplayName("Should find enrollments by academic year")
    void shouldFindEnrollmentsByAcademicYear() {
        // Arrange
        String academicYear = "2025-2026";
        when(jpaRepository.findByAcademicYear(academicYear)).thenReturn(List.of(testEntity));
        when(mapper.toDomain(testEntity)).thenReturn(testEnrollment);

        // Act
        List<Enrollment> result = adapter.findByAcademicYear(academicYear);

        // Assert
        assertThat(result).hasSize(1);
        verify(jpaRepository).findByAcademicYear(academicYear);
    }

    @Test
    @DisplayName("Should throw exception when academic year is null in findByAcademicYear")
    void shouldThrowExceptionWhenAcademicYearIsNullInFindByAcademicYear() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.findByAcademicYear(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Academic year cannot be null");

        verify(jpaRepository, never()).findByAcademicYear(any());
    }

    // ==================== Find By AcademicYear And Status Tests ====================

    @Test
    @DisplayName("Should find enrollments by academic year and status")
    void shouldFindEnrollmentsByAcademicYearAndStatus() {
        // Arrange
        String academicYear = "2025-2026";
        EnrollmentStatus status = EnrollmentStatus.ACTIVE;
        when(jpaRepository.findByAcademicYearAndStatus(academicYear, EnrollmentEntity.EnrollmentStatusEnum.ACTIVE))
            .thenReturn(List.of(testEntity));
        when(mapper.toDomain(testEntity)).thenReturn(testEnrollment);

        // Act
        List<Enrollment> result = adapter.findByAcademicYearAndStatus(academicYear, status);

        // Assert
        assertThat(result).hasSize(1);
        verify(jpaRepository).findByAcademicYearAndStatus(academicYear, EnrollmentEntity.EnrollmentStatusEnum.ACTIVE);
    }

    @Test
    @DisplayName("Should throw exception when academic year is null in findByAcademicYearAndStatus")
    void shouldThrowExceptionWhenAcademicYearIsNullInFindByAcademicYearAndStatus() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.findByAcademicYearAndStatus(null, EnrollmentStatus.ACTIVE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Academic year cannot be null");

        verify(jpaRepository, never()).findByAcademicYearAndStatus(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when status is null in findByAcademicYearAndStatus")
    void shouldThrowExceptionWhenStatusIsNullInFindByAcademicYearAndStatus() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.findByAcademicYearAndStatus("2025-2026", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Status cannot be null");

        verify(jpaRepository, never()).findByAcademicYearAndStatus(any(), any());
    }

    // ==================== Delete Tests ====================

    @Test
    @DisplayName("Should delete enrollment by id successfully")
    void shouldDeleteEnrollmentByIdSuccessfully() {
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

    // ==================== Count By StudentId Tests ====================

    @Test
    @DisplayName("Should count enrollments by student id")
    void shouldCountEnrollmentsByStudentId() {
        // Arrange
        Long studentId = 1L;
        when(jpaRepository.countByStudentId(studentId)).thenReturn(3L);

        // Act
        long result = adapter.countByStudentId(studentId);

        // Assert
        assertThat(result).isEqualTo(3L);
        verify(jpaRepository).countByStudentId(studentId);
    }

    @Test
    @DisplayName("Should throw exception when student id is null in countByStudentId")
    void shouldThrowExceptionWhenStudentIdIsNullInCountByStudentId() {
        // Act & Assert
        assertThatThrownBy(() -> adapter.countByStudentId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Student ID cannot be null");

        verify(jpaRepository, never()).countByStudentId(any());
    }
}
