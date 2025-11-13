package com.school.management.application.service;

import com.school.management.application.dto.guardian.CreateGuardianRequest;
import com.school.management.application.dto.guardian.UpdateGuardianRequest;
import com.school.management.domain.student.*;
import com.school.management.infrastructure.persistence.GuardianRepository;
import com.school.management.infrastructure.persistence.StudentRepository;
import com.school.management.infrastructure.security.EncryptionService;
import com.school.management.shared.exception.BusinessException;
import com.school.management.shared.exception.NotFoundException;
import com.school.management.shared.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GuardianService
 * Following TDD methodology - tests written FIRST
 *
 * @author School Management System
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GuardianService Unit Tests")
class GuardianServiceTest {

    @Mock
    private GuardianRepository guardianRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private GuardianService guardianService;

    private Student testStudent;
    private Guardian testGuardian;

    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setStudentCode("STU-2025-00001");
        testStudent.setStatus(StudentStatus.ACTIVE);

        testGuardian = new Guardian();
        testGuardian.setId(1L);
        testGuardian.setStudent(testStudent);
        testGuardian.setRelationship(Relationship.FATHER);
        testGuardian.setFirstName("John");
        testGuardian.setLastName("Doe");
        testGuardian.setMobile("9876543210");
        testGuardian.setEmail("john@example.com");
        testGuardian.setIsPrimary(true);
    }

    @Test
    @DisplayName("Should add guardian successfully")
    void shouldAddGuardianSuccessfully() {
        // Given
        Long studentId = 1L;
        CreateGuardianRequest request = createGuardianRequest(
            Relationship.FATHER, "John", "Doe", "9876543210", true);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(guardianRepository.existsByMobileHash(anyString())).thenReturn(false);
        when(encryptionService.encrypt(anyString())).thenReturn(new byte[]{1, 2, 3});
        when(encryptionService.hashMobile(anyString())).thenReturn("hashed_mobile");
        when(guardianRepository.save(any(Guardian.class))).thenReturn(testGuardian);

        // When
        Guardian result = guardianService.addGuardian(studentId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRelationship()).isEqualTo(Relationship.FATHER);
        verify(studentRepository).findById(studentId);
        verify(guardianRepository).save(any(Guardian.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when student not found")
    void shouldThrowNotFoundExceptionWhenStudentNotFound() {
        // Given
        Long studentId = 999L;
        CreateGuardianRequest request = createGuardianRequest(
            Relationship.FATHER, "John", "Doe", "9876543210", true);

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> guardianService.addGuardian(studentId, request))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Student not found");

        verify(guardianRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when adding duplicate relationship")
    void shouldThrowBusinessExceptionForDuplicateRelationship() {
        // Given
        Long studentId = 1L;
        CreateGuardianRequest request = createGuardianRequest(
            Relationship.FATHER, "John", "Doe", "9876543210", true);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(guardianRepository.findByStudentAndRelationship(testStudent, Relationship.FATHER))
            .thenReturn(Optional.of(testGuardian));

        // When/Then
        assertThatThrownBy(() -> guardianService.addGuardian(studentId, request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("already has a guardian with relationship");

        verify(guardianRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update existing primary guardian when adding new primary")
    void shouldUpdateExistingPrimaryGuardianWhenAddingNewPrimary() {
        // Given
        Long studentId = 1L;
        CreateGuardianRequest request = createGuardianRequest(
            Relationship.MOTHER, "Jane", "Doe", "9876543211", true);

        Guardian existingPrimary = new Guardian();
        existingPrimary.setId(2L);
        existingPrimary.setStudent(testStudent);
        existingPrimary.setRelationship(Relationship.FATHER);
        existingPrimary.setIsPrimary(true);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(guardianRepository.findByStudentAndIsPrimary(testStudent, true))
            .thenReturn(Optional.of(existingPrimary));
        when(guardianRepository.existsByMobileHash(anyString())).thenReturn(false);
        when(encryptionService.encrypt(anyString())).thenReturn(new byte[]{1, 2, 3});
        when(encryptionService.hashMobile(anyString())).thenReturn("hashed_mobile");
        when(guardianRepository.save(any(Guardian.class))).thenReturn(testGuardian);

        // When
        Guardian result = guardianService.addGuardian(studentId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(existingPrimary.getIsPrimary()).isFalse();
        verify(guardianRepository, times(2)).save(any(Guardian.class)); // Once for update, once for new
    }

    @Test
    @DisplayName("Should update guardian successfully")
    void shouldUpdateGuardianSuccessfully() {
        // Given
        Long guardianId = 1L;
        UpdateGuardianRequest request = new UpdateGuardianRequest();
        request.setEmail("newemail@example.com");
        request.setOccupation("Engineer");

        when(guardianRepository.findById(guardianId)).thenReturn(Optional.of(testGuardian));
        when(encryptionService.encrypt(anyString())).thenReturn(new byte[]{1, 2, 3});
        when(guardianRepository.save(any(Guardian.class))).thenReturn(testGuardian);

        // When
        Guardian result = guardianService.updateGuardian(guardianId, request);

        // Then
        assertThat(result).isNotNull();
        verify(guardianRepository).save(any(Guardian.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when updating non-existent guardian")
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistentGuardian() {
        // Given
        Long guardianId = 999L;
        UpdateGuardianRequest request = new UpdateGuardianRequest();

        when(guardianRepository.findById(guardianId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> guardianService.updateGuardian(guardianId, request))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Guardian not found");

        verify(guardianRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update primary guardian successfully")
    void shouldUpdatePrimaryGuardianSuccessfully() {
        // Given
        Long studentId = 1L;
        Long newPrimaryGuardianId = 2L;

        Guardian newPrimary = new Guardian();
        newPrimary.setId(newPrimaryGuardianId);
        newPrimary.setStudent(testStudent);
        newPrimary.setRelationship(Relationship.MOTHER);
        newPrimary.setIsPrimary(false);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(guardianRepository.findById(newPrimaryGuardianId)).thenReturn(Optional.of(newPrimary));
        when(guardianRepository.findByStudentAndIsPrimary(testStudent, true))
            .thenReturn(Optional.of(testGuardian));
        when(guardianRepository.save(any(Guardian.class))).thenReturn(newPrimary);

        // When
        Guardian result = guardianService.updatePrimaryGuardian(studentId, newPrimaryGuardianId);

        // Then
        assertThat(result).isNotNull();
        assertThat(newPrimary.getIsPrimary()).isTrue();
        assertThat(testGuardian.getIsPrimary()).isFalse();
        verify(guardianRepository, times(2)).save(any(Guardian.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when guardian does not belong to student")
    void shouldThrowValidationExceptionWhenGuardianDoesNotBelongToStudent() {
        // Given
        Long studentId = 1L;
        Long guardianId = 2L;

        Student differentStudent = new Student();
        differentStudent.setId(2L);

        Guardian guardian = new Guardian();
        guardian.setId(guardianId);
        guardian.setStudent(differentStudent);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(guardianRepository.findById(guardianId)).thenReturn(Optional.of(guardian));

        // When/Then
        assertThatThrownBy(() -> guardianService.updatePrimaryGuardian(studentId, guardianId))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("does not belong to the specified student");

        verify(guardianRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should remove guardian successfully")
    void shouldRemoveGuardianSuccessfully() {
        // Given
        Long guardianId = 1L;

        Guardian anotherGuardian = new Guardian();
        anotherGuardian.setId(2L);
        anotherGuardian.setStudent(testStudent);

        when(guardianRepository.findById(guardianId)).thenReturn(Optional.of(testGuardian));
        when(guardianRepository.countByStudent(testStudent)).thenReturn(2L);

        // When
        guardianService.removeGuardian(guardianId);

        // Then
        verify(guardianRepository).deleteById(guardianId);
    }

    @Test
    @DisplayName("Should throw BusinessException when removing last guardian")
    void shouldThrowBusinessExceptionWhenRemovingLastGuardian() {
        // Given
        Long guardianId = 1L;

        when(guardianRepository.findById(guardianId)).thenReturn(Optional.of(testGuardian));
        when(guardianRepository.countByStudent(testStudent)).thenReturn(1L);

        // When/Then
        assertThatThrownBy(() -> guardianService.removeGuardian(guardianId))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Cannot remove the last guardian");

        verify(guardianRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should get all guardians for student")
    void shouldGetAllGuardiansForStudent() {
        // Given
        Long studentId = 1L;
        Guardian mother = new Guardian();
        mother.setRelationship(Relationship.MOTHER);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(guardianRepository.findByStudent(testStudent))
            .thenReturn(Arrays.asList(testGuardian, mother));

        // When
        List<Guardian> result = guardianService.getGuardiansByStudentId(studentId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Guardian::getRelationship)
            .containsExactlyInAnyOrder(Relationship.FATHER, Relationship.MOTHER);
    }

    @Test
    @DisplayName("Should get primary guardian for student")
    void shouldGetPrimaryGuardianForStudent() {
        // Given
        Long studentId = 1L;

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(guardianRepository.findByStudentAndIsPrimary(testStudent, true))
            .thenReturn(Optional.of(testGuardian));

        // When
        Optional<Guardian> result = guardianService.getPrimaryGuardian(studentId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getIsPrimary()).isTrue();
        assertThat(result.get().getRelationship()).isEqualTo(Relationship.FATHER);
    }

    @Test
    @DisplayName("Should throw ValidationException for duplicate mobile number")
    void shouldThrowValidationExceptionForDuplicateMobileNumber() {
        // Given
        Long studentId = 1L;
        CreateGuardianRequest request = createGuardianRequest(
            Relationship.FATHER, "John", "Doe", "9876543210", true);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(encryptionService.hashMobile(anyString())).thenReturn("hashed_mobile");
        when(guardianRepository.existsByMobileHash("hashed_mobile")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> guardianService.addGuardian(studentId, request))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Mobile number already in use");

        verify(guardianRepository, never()).save(any());
    }

    private CreateGuardianRequest createGuardianRequest(Relationship relationship,
                                                        String firstName,
                                                        String lastName,
                                                        String mobile,
                                                        boolean isPrimary) {
        CreateGuardianRequest request = new CreateGuardianRequest();
        request.setRelationship(relationship);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setMobile(mobile);
        request.setEmail(firstName.toLowerCase() + "@example.com");
        request.setOccupation("Business");
        request.setIsPrimary(isPrimary);
        return request;
    }
}
