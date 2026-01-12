package com.schoolms.student.application.service;

import com.schoolms.student.application.mapper.StudentMapper;
import com.schoolms.student.domain.exception.DuplicateAdhaarException;
import com.schoolms.student.domain.exception.DuplicateEmailException;
import com.schoolms.student.domain.exception.DuplicatePhoneException;
import com.schoolms.student.domain.exception.StudentNotFoundException;
import com.schoolms.student.domain.model.Student;
import com.schoolms.student.domain.model.StudentId;
import com.schoolms.student.domain.model.StudentStatus;
import com.schoolms.student.domain.repository.StudentRepository;
import com.schoolms.student.infrastructure.cache.RedisCacheManager;
import com.schoolms.student.infrastructure.generator.StudentIdGenerator;
import com.schoolms.student.presentation.dto.request.CreateStudentRequest;
import com.schoolms.student.presentation.dto.request.UpdateStudentRequest;
import com.schoolms.student.presentation.dto.response.StudentListResponse;
import com.schoolms.student.presentation.dto.response.StudentResponse;
import com.schoolms.student.presentation.dto.response.StudentStatisticsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentApplicationService (QA-006 to QA-010)
 * Tests service layer logic with mocked dependencies
 *
 * Coverage:
 * - Create student with valid data
 * - Update editable fields only
 * - Delete existing student
 * - Get student by ID
 * - Exception handling (StudentNotFoundException, DuplicatePhoneException, etc.)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentApplicationService Tests")
class StudentApplicationServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private RedisCacheManager cacheManager;

    @Mock
    private StudentIdGenerator idGenerator;

    @InjectMocks
    private StudentApplicationService service;

    private CreateStudentRequest validCreateRequest;
    private Student validStudent;
    private StudentResponse validStudentResponse;

    @BeforeEach
    void setUp() {
        validCreateRequest = new CreateStudentRequest(
            "John",
            "Doe",
            LocalDate.now().minusYears(10),
            "123456789012",
            "9876543210",
            "john.doe@example.com",
            "123 Main Street",
            "Robert Doe",
            "Jane Doe",
            "Mole on left hand"
        );

        validStudent = Student.register(
            "STU-2026-00001",
            "John",
            "Doe",
            LocalDate.now().minusYears(10),
            "123456789012",
            "9876543210",
            "john.doe@example.com",
            "123 Main Street",
            "Robert Doe",
            "Jane Doe",
            "Mole on left hand"
        );

        validStudentResponse = new StudentResponse(
            "STU-2026-00001",
            "John",
            "Doe",
            LocalDate.now().minusYears(10),
            10,
            "123456789012",
            "123 Main Street",
            "Mole on left hand",
            "Robert Doe",
            "Jane Doe",
            "9876543210",
            "john.doe@example.com",
            StudentStatus.ACTIVE,
            null,
            null
        );
    }

    @Nested
    @DisplayName("Create Student Tests")
    class CreateStudentTests {

        @Test
        @DisplayName("Should create student successfully with valid data")
        void shouldCreateStudentSuccessfully() {
            // Given
            when(studentRepository.existsByPhone(anyString())).thenReturn(false);
            when(studentRepository.existsByEmail(anyString())).thenReturn(false);
            when(studentRepository.existsByAdhaarNumber(anyString())).thenReturn(false);
            when(idGenerator.generateNextStudentId()).thenReturn("STU-2026-00001");
            when(studentRepository.save(any(Student.class))).thenReturn(validStudent);
            when(studentMapper.toResponse(any(Student.class))).thenReturn(validStudentResponse);

            // When
            StudentResponse response = service.createStudent(validCreateRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo("STU-2026-00001");
            assertThat(response.firstName()).isEqualTo("John");
            assertThat(response.lastName()).isEqualTo("Doe");
            assertThat(response.status()).isEqualTo(StudentStatus.ACTIVE);

            // Verify interactions
            verify(studentRepository).existsByPhone("9876543210");
            verify(studentRepository).existsByEmail("john.doe@example.com");
            verify(studentRepository).existsByAdhaarNumber("123456789012");
            verify(idGenerator).generateNextStudentId();
            verify(studentRepository).save(any(Student.class));
            verify(cacheManager).evictAllStudentCaches();
            verify(studentMapper).toResponse(any(Student.class));
        }

        @Test
        @DisplayName("Should throw DuplicatePhoneException when phone already exists")
        void shouldThrowDuplicatePhoneException() {
            // Given
            when(studentRepository.existsByPhone(anyString())).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> service.createStudent(validCreateRequest))
                .isInstanceOf(DuplicatePhoneException.class);

            // Verify no further processing
            verify(studentRepository, never()).save(any());
            verify(cacheManager, never()).evictAllStudentCaches();
        }

        @Test
        @DisplayName("Should throw DuplicateEmailException when email already exists")
        void shouldThrowDuplicateEmailException() {
            // Given
            when(studentRepository.existsByPhone(anyString())).thenReturn(false);
            when(studentRepository.existsByEmail(anyString())).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> service.createStudent(validCreateRequest))
                .isInstanceOf(DuplicateEmailException.class);

            // Verify no save occurred
            verify(studentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw DuplicateAdhaarException when adhaar already exists")
        void shouldThrowDuplicateAdhaarException() {
            // Given
            when(studentRepository.existsByPhone(anyString())).thenReturn(false);
            when(studentRepository.existsByEmail(anyString())).thenReturn(false);
            when(studentRepository.existsByAdhaarNumber(anyString())).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> service.createStudent(validCreateRequest))
                .isInstanceOf(DuplicateAdhaarException.class);

            // Verify no save occurred
            verify(studentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should generate unique student ID when creating")
        void shouldGenerateUniqueStudentId() {
            // Given
            when(studentRepository.existsByPhone(anyString())).thenReturn(false);
            when(studentRepository.existsByEmail(anyString())).thenReturn(false);
            when(studentRepository.existsByAdhaarNumber(anyString())).thenReturn(false);
            when(idGenerator.generateNextStudentId()).thenReturn("STU-2026-00042");
            when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(studentMapper.toResponse(any(Student.class))).thenReturn(validStudentResponse);

            // When
            service.createStudent(validCreateRequest);

            // Then
            verify(idGenerator).generateNextStudentId();
        }

        @Test
        @DisplayName("Should evict cache after creating student")
        void shouldEvictCacheAfterCreation() {
            // Given
            when(studentRepository.existsByPhone(anyString())).thenReturn(false);
            when(studentRepository.existsByEmail(anyString())).thenReturn(false);
            when(studentRepository.existsByAdhaarNumber(anyString())).thenReturn(false);
            when(idGenerator.generateNextStudentId()).thenReturn("STU-2026-00001");
            when(studentRepository.save(any(Student.class))).thenReturn(validStudent);
            when(studentMapper.toResponse(any(Student.class))).thenReturn(validStudentResponse);

            // When
            service.createStudent(validCreateRequest);

            // Then
            verify(cacheManager).evictAllStudentCaches();
        }
    }

    @Nested
    @DisplayName("Update Student Tests")
    class UpdateStudentTests {

        @Test
        @DisplayName("Should update student successfully")
        void shouldUpdateStudentSuccessfully() {
            // Given
            UpdateStudentRequest updateRequest = new UpdateStudentRequest(
                "Jonathan",
                "Smith",
                "9123456789",
                null
            );
            StudentId studentId = new StudentId("STU-2026-00001");

            when(studentRepository.findById(studentId)).thenReturn(Optional.of(validStudent));
            when(studentRepository.existsByPhone("9123456789")).thenReturn(false);
            when(studentRepository.save(any(Student.class))).thenReturn(validStudent);
            when(studentMapper.toResponse(any(Student.class))).thenReturn(validStudentResponse);

            // When
            StudentResponse response = service.updateStudent("STU-2026-00001", updateRequest);

            // Then
            assertThat(response).isNotNull();
            verify(studentRepository).findById(studentId);
            verify(studentRepository).save(any(Student.class));
            verify(cacheManager).evictStudent("STU-2026-00001");
            verify(cacheManager).evictAllStudentCaches();
        }

        @Test
        @DisplayName("Should throw StudentNotFoundException when student not found")
        void shouldThrowStudentNotFoundException() {
            // Given
            UpdateStudentRequest updateRequest = new UpdateStudentRequest("John", "Doe", null, null);
            StudentId studentId = new StudentId("STU-2026-99999");

            when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> service.updateStudent("STU-2026-99999", updateRequest))
                .isInstanceOf(StudentNotFoundException.class);

            verify(studentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw DuplicatePhoneException when updating to existing phone")
        void shouldThrowDuplicatePhoneExceptionOnUpdate() {
            // Given
            UpdateStudentRequest updateRequest = new UpdateStudentRequest(
                null,
                null,
                "9999999999",
                null
            );
            StudentId studentId = new StudentId("STU-2026-00001");

            when(studentRepository.findById(studentId)).thenReturn(Optional.of(validStudent));
            when(studentRepository.existsByPhone("9999999999")).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> service.updateStudent("STU-2026-00001", updateRequest))
                .isInstanceOf(DuplicatePhoneException.class);

            verify(studentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update student status to INACTIVE")
        void shouldUpdateStatusToInactive() {
            // Given
            UpdateStudentRequest updateRequest = new UpdateStudentRequest(
                null,
                null,
                null,
                StudentStatus.INACTIVE
            );
            StudentId studentId = new StudentId("STU-2026-00001");

            when(studentRepository.findById(studentId)).thenReturn(Optional.of(validStudent));
            when(studentRepository.save(any(Student.class))).thenReturn(validStudent);
            when(studentMapper.toResponse(any(Student.class))).thenReturn(validStudentResponse);

            // When
            service.updateStudent("STU-2026-00001", updateRequest);

            // Then
            verify(studentRepository).save(any(Student.class));
        }

        @Test
        @DisplayName("Should not check phone uniqueness if phone unchanged")
        void shouldNotCheckPhoneUniquenessIfUnchanged() {
            // Given
            UpdateStudentRequest updateRequest = new UpdateStudentRequest(
                "Jonathan",
                null,
                "9876543210", // Same as original
                null
            );
            StudentId studentId = new StudentId("STU-2026-00001");

            when(studentRepository.findById(studentId)).thenReturn(Optional.of(validStudent));
            when(studentRepository.save(any(Student.class))).thenReturn(validStudent);
            when(studentMapper.toResponse(any(Student.class))).thenReturn(validStudentResponse);

            // When
            service.updateStudent("STU-2026-00001", updateRequest);

            // Then
            verify(studentRepository, never()).existsByPhone(anyString());
        }
    }

    @Nested
    @DisplayName("Delete Student Tests")
    class DeleteStudentTests {

        @Test
        @DisplayName("Should delete student successfully")
        void shouldDeleteStudentSuccessfully() {
            // Given
            StudentId studentId = new StudentId("STU-2026-00001");
            when(studentRepository.findById(studentId)).thenReturn(Optional.of(validStudent));

            // When
            service.deleteStudent("STU-2026-00001");

            // Then
            verify(studentRepository).findById(studentId);
            verify(studentRepository).delete(studentId);
            verify(cacheManager).evictStudent("STU-2026-00001");
            verify(cacheManager).evictAllStudentCaches();
        }

        @Test
        @DisplayName("Should throw StudentNotFoundException when deleting non-existent student")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            // Given
            StudentId studentId = new StudentId("STU-2026-99999");
            when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> service.deleteStudent("STU-2026-99999"))
                .isInstanceOf(StudentNotFoundException.class);

            verify(studentRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should evict cache after deleting student")
        void shouldEvictCacheAfterDeletion() {
            // Given
            StudentId studentId = new StudentId("STU-2026-00001");
            when(studentRepository.findById(studentId)).thenReturn(Optional.of(validStudent));

            // When
            service.deleteStudent("STU-2026-00001");

            // Then
            verify(cacheManager).evictStudent("STU-2026-00001");
            verify(cacheManager).evictAllStudentCaches();
        }
    }

    @Nested
    @DisplayName("Get Student Tests")
    class GetStudentTests {

        @Test
        @DisplayName("Should get student from database when not in cache")
        void shouldGetStudentFromDatabase() {
            // Given
            StudentId studentId = new StudentId("STU-2026-00001");
            when(cacheManager.getStudent("STU-2026-00001", StudentResponse.class)).thenReturn(null);
            when(studentRepository.findById(studentId)).thenReturn(Optional.of(validStudent));
            when(studentMapper.toResponse(validStudent)).thenReturn(validStudentResponse);

            // When
            StudentResponse response = service.getStudent("STU-2026-00001");

            // Then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo("STU-2026-00001");
            verify(cacheManager).getStudent("STU-2026-00001", StudentResponse.class);
            verify(studentRepository).findById(studentId);
            verify(cacheManager).cacheStudent("STU-2026-00001", validStudentResponse);
        }

        @Test
        @DisplayName("Should get student from cache when available")
        void shouldGetStudentFromCache() {
            // Given
            when(cacheManager.getStudent("STU-2026-00001", StudentResponse.class))
                .thenReturn(validStudentResponse);

            // When
            StudentResponse response = service.getStudent("STU-2026-00001");

            // Then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo("STU-2026-00001");
            verify(cacheManager).getStudent("STU-2026-00001", StudentResponse.class);
            verify(studentRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should throw StudentNotFoundException when student not found")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            StudentId studentId = new StudentId("STU-2026-99999");
            when(cacheManager.getStudent("STU-2026-99999", StudentResponse.class)).thenReturn(null);
            when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> service.getStudent("STU-2026-99999"))
                .isInstanceOf(StudentNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("List Students Tests")
    class ListStudentsTests {

        @Test
        @DisplayName("Should list all students when no filters")
        void shouldListAllStudents() {
            // Given
            List<Student> students = Arrays.asList(validStudent);
            List<StudentResponse> responses = Arrays.asList(validStudentResponse);

            when(studentRepository.findAll()).thenReturn(students);
            when(studentMapper.toResponseList(students)).thenReturn(responses);
            when(studentRepository.count()).thenReturn(1L);
            when(studentRepository.countByStatus(StudentStatus.ACTIVE)).thenReturn(1L);
            when(studentRepository.countByStatus(StudentStatus.INACTIVE)).thenReturn(0L);

            // When
            StudentListResponse response = service.listStudents(null, null);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.students()).hasSize(1);
            assertThat(response.totalCount()).isEqualTo(1);
            assertThat(response.activeCount()).isEqualTo(1);
            assertThat(response.inactiveCount()).isEqualTo(0);
            verify(studentRepository).findAll();
        }

        @Test
        @DisplayName("Should search students by query")
        void shouldSearchStudents() {
            // Given
            List<Student> students = Arrays.asList(validStudent);
            List<StudentResponse> responses = Arrays.asList(validStudentResponse);

            when(studentRepository.search("John")).thenReturn(students);
            when(studentMapper.toResponseList(students)).thenReturn(responses);
            when(studentRepository.count()).thenReturn(1L);
            when(studentRepository.countByStatus(StudentStatus.ACTIVE)).thenReturn(1L);
            when(studentRepository.countByStatus(StudentStatus.INACTIVE)).thenReturn(0L);

            // When
            StudentListResponse response = service.listStudents("John", null);

            // Then
            assertThat(response.students()).hasSize(1);
            verify(studentRepository).search("John");
        }

        @Test
        @DisplayName("Should filter students by status")
        void shouldFilterByStatus() {
            // Given
            List<Student> students = Arrays.asList(validStudent);
            List<StudentResponse> responses = Arrays.asList(validStudentResponse);

            when(studentRepository.findByStatus(StudentStatus.ACTIVE)).thenReturn(students);
            when(studentMapper.toResponseList(students)).thenReturn(responses);
            when(studentRepository.count()).thenReturn(1L);
            when(studentRepository.countByStatus(StudentStatus.ACTIVE)).thenReturn(1L);
            when(studentRepository.countByStatus(StudentStatus.INACTIVE)).thenReturn(0L);

            // When
            StudentListResponse response = service.listStudents(null, StudentStatus.ACTIVE);

            // Then
            assertThat(response.students()).hasSize(1);
            verify(studentRepository).findByStatus(StudentStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("Get Statistics Tests")
    class GetStatisticsTests {

        @Test
        @DisplayName("Should get student statistics")
        void shouldGetStatistics() {
            // Given
            when(studentRepository.count()).thenReturn(10L);
            when(studentRepository.countByStatus(StudentStatus.ACTIVE)).thenReturn(8L);
            when(studentRepository.countByStatus(StudentStatus.INACTIVE)).thenReturn(2L);

            // When
            StudentStatisticsResponse response = service.getStatistics();

            // Then
            assertThat(response).isNotNull();
            assertThat(response.totalStudents()).isEqualTo(10);
            assertThat(response.activeStudents()).isEqualTo(8);
            assertThat(response.inactiveStudents()).isEqualTo(2);
        }
    }
}
