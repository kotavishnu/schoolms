package com.school.management.service;

import com.school.management.dto.request.ClassRequestDTO;
import com.school.management.dto.response.ClassResponseDTO;
import com.school.management.exception.DuplicateResourceException;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.exception.ValidationException;
import com.school.management.mapper.ClassMapper;
import com.school.management.model.SchoolClass;
import com.school.management.repository.ClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClassService
 *
 * Uses Mockito to mock dependencies and test business logic.
 *
 * @author School Management Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClassService Tests")
class ClassServiceTest {

    @Mock
    private ClassRepository classRepository;

    @Mock
    private ClassMapper classMapper;

    @InjectMocks
    private ClassService classService;

    private ClassRequestDTO requestDTO;
    private SchoolClass schoolClass;
    private ClassResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        requestDTO = ClassRequestDTO.builder()
                .classNumber(5)
                .section("A")
                .academicYear("2024-2025")
                .capacity(50)
                .classTeacher("Mrs. Sharma")
                .build();

        schoolClass = SchoolClass.builder()
                .id(1L)
                .classNumber(5)
                .section("A")
                .academicYear("2024-2025")
                .capacity(50)
                .currentEnrollment(20)
                .classTeacher("Mrs. Sharma")
                .build();

        responseDTO = ClassResponseDTO.builder()
                .id(1L)
                .classNumber(5)
                .section("A")
                .displayName("Class 5 - A")
                .academicYear("2024-2025")
                .capacity(50)
                .currentEnrollment(20)
                .availableSeats(30)
                .classTeacher("Mrs. Sharma")
                .build();
    }

    @Test
    @DisplayName("Should create class successfully")
    void shouldCreateClassSuccessfully() {
        // Given
        when(classRepository.existsByClassNumberAndSectionAndAcademicYear(5, "A", "2024-2025"))
                .thenReturn(false);
        when(classMapper.toEntity(any(ClassRequestDTO.class))).thenReturn(schoolClass);
        when(classRepository.save(any(SchoolClass.class))).thenReturn(schoolClass);
        when(classMapper.toResponseDTO(any(SchoolClass.class))).thenReturn(responseDTO);

        // When
        ClassResponseDTO result = classService.createClass(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getClassNumber()).isEqualTo(5);
        assertThat(result.getSection()).isEqualTo("A");
        assertThat(result.getAcademicYear()).isEqualTo("2024-2025");

        verify(classRepository).existsByClassNumberAndSectionAndAcademicYear(5, "A", "2024-2025");
        verify(classRepository).save(any(SchoolClass.class));
    }

    @Test
    @DisplayName("Should throw exception when duplicate class exists")
    void shouldThrowExceptionWhenDuplicateClass() {
        // Given
        when(classRepository.existsByClassNumberAndSectionAndAcademicYear(5, "A", "2024-2025"))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> classService.createClass(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("SchoolClass");

        verify(classRepository).existsByClassNumberAndSectionAndAcademicYear(5, "A", "2024-2025");
        verify(classRepository, never()).save(any(SchoolClass.class));
    }

    @Test
    @DisplayName("Should get class by ID successfully")
    void shouldGetClassByIdSuccessfully() {
        // Given
        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(classMapper.toResponseDTO(any(SchoolClass.class))).thenReturn(responseDTO);

        // When
        ClassResponseDTO result = classService.getClass(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getClassNumber()).isEqualTo(5);

        verify(classRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when class not found")
    void shouldThrowExceptionWhenClassNotFound() {
        // Given
        when(classRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> classService.getClass(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("SchoolClass")
                .hasMessageContaining("999");

        verify(classRepository).findById(999L);
    }

    @Test
    @DisplayName("Should get all classes successfully")
    void shouldGetAllClassesSuccessfully() {
        // Given
        List<SchoolClass> classList = new ArrayList<>();
        classList.add(schoolClass);

        List<ClassResponseDTO> responseDTOList = new ArrayList<>();
        responseDTOList.add(responseDTO);

        when(classRepository.findAll()).thenReturn(classList);
        when(classMapper.toResponseDTOList(anyList())).thenReturn(responseDTOList);

        // When
        List<ClassResponseDTO> result = classService.getAllClasses();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClassNumber()).isEqualTo(5);

        verify(classRepository).findAll();
    }

    @Test
    @DisplayName("Should get classes by academic year")
    void shouldGetClassesByAcademicYear() {
        // Given
        List<SchoolClass> classList = new ArrayList<>();
        classList.add(schoolClass);

        List<ClassResponseDTO> responseDTOList = new ArrayList<>();
        responseDTOList.add(responseDTO);

        when(classRepository.findAllByAcademicYearOrdered("2024-2025")).thenReturn(classList);
        when(classMapper.toResponseDTOList(anyList())).thenReturn(responseDTOList);

        // When
        List<ClassResponseDTO> result = classService.getClassesByAcademicYear("2024-2025");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(classRepository).findAllByAcademicYearOrdered("2024-2025");
    }

    @Test
    @DisplayName("Should update class successfully")
    void shouldUpdateClassSuccessfully() {
        // Given
        ClassRequestDTO updateDTO = ClassRequestDTO.builder()
                .classNumber(5)
                .section("A")
                .academicYear("2024-2025")
                .capacity(60) // Increased capacity
                .classTeacher("Mr. Kumar")
                .build();

        SchoolClass updatedClass = SchoolClass.builder()
                .id(1L)
                .classNumber(5)
                .section("A")
                .academicYear("2024-2025")
                .capacity(60)
                .currentEnrollment(20)
                .classTeacher("Mr. Kumar")
                .build();

        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(classRepository.save(any(SchoolClass.class))).thenReturn(updatedClass);
        when(classMapper.toResponseDTO(any(SchoolClass.class))).thenReturn(responseDTO);
        doNothing().when(classMapper).updateEntityFromDTO(any(ClassRequestDTO.class), any(SchoolClass.class));

        // When
        ClassResponseDTO result = classService.updateClass(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();

        verify(classRepository).findById(1L);
        verify(classMapper).updateEntityFromDTO(updateDTO, schoolClass);
        verify(classRepository).save(schoolClass);
    }

    @Test
    @DisplayName("Should throw exception when update capacity less than current enrollment")
    void shouldThrowExceptionWhenUpdateCapacityLessThanEnrollment() {
        // Given
        ClassRequestDTO updateDTO = ClassRequestDTO.builder()
                .classNumber(5)
                .section("A")
                .academicYear("2024-2025")
                .capacity(10) // Less than current enrollment of 20
                .classTeacher("Mr. Kumar")
                .build();

        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));

        // When & Then
        assertThatThrownBy(() -> classService.updateClass(1L, updateDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("capacity")
                .hasMessageContaining("enrollment");

        verify(classRepository).findById(1L);
        verify(classRepository, never()).save(any(SchoolClass.class));
    }

    @Test
    @DisplayName("Should delete class successfully")
    void shouldDeleteClassSuccessfully() {
        // Given
        SchoolClass emptyClass = SchoolClass.builder()
                .id(1L)
                .classNumber(5)
                .section("A")
                .academicYear("2024-2025")
                .capacity(50)
                .currentEnrollment(0) // No students
                .classTeacher("Mrs. Sharma")
                .build();

        when(classRepository.findById(1L)).thenReturn(Optional.of(emptyClass));

        // When
        classService.deleteClass(1L);

        // Then
        verify(classRepository).findById(1L);
        verify(classRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting class with students")
    void shouldThrowExceptionWhenDeletingClassWithStudents() {
        // Given
        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass)); // Has 20 students

        // When & Then
        assertThatThrownBy(() -> classService.deleteClass(1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Cannot delete")
                .hasMessageContaining("students");

        verify(classRepository).findById(1L);
        verify(classRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should check if class exists")
    void shouldCheckIfClassExists() {
        // Given
        when(classRepository.existsByClassNumberAndSectionAndAcademicYear(5, "A", "2024-2025"))
                .thenReturn(true);

        // When
        boolean exists = classService.classExists(5, "A", "2024-2025");

        // Then
        assertThat(exists).isTrue();

        verify(classRepository).existsByClassNumberAndSectionAndAcademicYear(5, "A", "2024-2025");
    }
}
