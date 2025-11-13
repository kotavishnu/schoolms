package com.school.management.application.service;

import com.school.management.application.dto.guardian.CreateGuardianRequest;
import com.school.management.application.dto.guardian.UpdateGuardianRequest;
import com.school.management.domain.student.Guardian;
import com.school.management.domain.student.Student;
import com.school.management.infrastructure.persistence.GuardianRepository;
import com.school.management.infrastructure.persistence.StudentRepository;
import com.school.management.infrastructure.security.EncryptionService;
import com.school.management.shared.exception.BusinessException;
import com.school.management.shared.exception.NotFoundException;
import com.school.management.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Guardian management
 * Implements business logic for guardian operations
 *
 * Business Rules Enforced:
 * - At least one guardian per student
 * - Only one primary guardian per student
 * - One guardian per relationship type per student
 * - Mobile number uniqueness
 *
 * @author School Management System
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GuardianService {

    private final GuardianRepository guardianRepository;
    private final StudentRepository studentRepository;
    private final EncryptionService encryptionService;

    /**
     * Add a new guardian to a student
     *
     * @param studentId the student ID
     * @param request the guardian creation request
     * @return the created guardian
     * @throws NotFoundException if student not found
     * @throws BusinessException if duplicate relationship
     * @throws ValidationException if mobile number already in use
     */
    public Guardian addGuardian(Long studentId, CreateGuardianRequest request) {
        log.debug("Adding guardian for student ID: {}", studentId);

        // Find student
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentId));

        // Check for duplicate relationship
        Optional<Guardian> existingRelationship = guardianRepository
            .findByStudentAndRelationship(student, request.getRelationship());

        if (existingRelationship.isPresent()) {
            throw new BusinessException(
                String.format("Student already has a guardian with relationship: %s",
                    request.getRelationship()));
        }

        // Validate mobile uniqueness
        String mobileHash = encryptionService.hashMobile(request.getMobile());
        if (guardianRepository.existsByMobileHash(mobileHash)) {
            throw new ValidationException("Mobile number already in use by another guardian");
        }

        // If this is a primary guardian, update existing primary to non-primary
        if (request.getIsPrimary()) {
            Optional<Guardian> existingPrimary = guardianRepository
                .findByStudentAndIsPrimary(student, true);

            if (existingPrimary.isPresent()) {
                log.debug("Updating existing primary guardian to non-primary");
                Guardian oldPrimary = existingPrimary.get();
                oldPrimary.setIsPrimary(false);
                guardianRepository.save(oldPrimary);
            }
        }

        // Create guardian
        Guardian guardian = new Guardian();
        guardian.setStudent(student);
        guardian.setRelationship(request.getRelationship());
        guardian.setFirstName(request.getFirstName());
        guardian.setLastName(request.getLastName());
        guardian.setMobile(request.getMobile());
        guardian.setEmail(request.getEmail());
        guardian.setOccupation(request.getOccupation());
        guardian.setIsPrimary(request.getIsPrimary());

        // Encrypt PII fields
        guardian.encryptFields(encryptionService);

        // Save guardian
        Guardian saved = guardianRepository.save(guardian);
        log.info("Guardian added successfully for student ID: {}", studentId);

        return saved;
    }

    /**
     * Update an existing guardian
     *
     * @param guardianId the guardian ID
     * @param request the update request
     * @return the updated guardian
     * @throws NotFoundException if guardian not found
     */
    public Guardian updateGuardian(Long guardianId, UpdateGuardianRequest request) {
        log.debug("Updating guardian ID: {}", guardianId);

        Guardian guardian = guardianRepository.findById(guardianId)
            .orElseThrow(() -> new NotFoundException("Guardian not found with ID: " + guardianId));

        // Update fields if provided
        if (request.getFirstName() != null) {
            guardian.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            guardian.setLastName(request.getLastName());
        }
        if (request.getMobile() != null) {
            // Validate mobile uniqueness
            String mobileHash = encryptionService.hashMobile(request.getMobile());
            if (guardianRepository.existsByMobileHash(mobileHash) &&
                !guardian.getMobileHash().equals(mobileHash)) {
                throw new ValidationException("Mobile number already in use by another guardian");
            }
            guardian.setMobile(request.getMobile());
        }
        if (request.getEmail() != null) {
            guardian.setEmail(request.getEmail());
        }
        if (request.getOccupation() != null) {
            guardian.setOccupation(request.getOccupation());
        }

        // Re-encrypt fields
        guardian.encryptFields(encryptionService);

        // Save updated guardian
        Guardian updated = guardianRepository.save(guardian);
        log.info("Guardian updated successfully: {}", guardianId);

        return updated;
    }

    /**
     * Update the primary guardian for a student
     * Sets the specified guardian as primary and updates the old primary to non-primary
     *
     * @param studentId the student ID
     * @param newPrimaryGuardianId the ID of the guardian to set as primary
     * @return the updated primary guardian
     * @throws NotFoundException if student or guardian not found
     * @throws ValidationException if guardian does not belong to the student
     */
    public Guardian updatePrimaryGuardian(Long studentId, Long newPrimaryGuardianId) {
        log.debug("Updating primary guardian for student ID: {} to guardian ID: {}",
            studentId, newPrimaryGuardianId);

        // Find student
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentId));

        // Find new primary guardian
        Guardian newPrimary = guardianRepository.findById(newPrimaryGuardianId)
            .orElseThrow(() -> new NotFoundException(
                "Guardian not found with ID: " + newPrimaryGuardianId));

        // Validate guardian belongs to student
        if (!newPrimary.getStudent().getId().equals(studentId)) {
            throw new ValidationException(
                "Guardian does not belong to the specified student");
        }

        // Update old primary to non-primary
        Optional<Guardian> oldPrimary = guardianRepository.findByStudentAndIsPrimary(student, true);
        if (oldPrimary.isPresent() && !oldPrimary.get().getId().equals(newPrimaryGuardianId)) {
            log.debug("Updating old primary guardian to non-primary");
            Guardian oldPrimaryGuardian = oldPrimary.get();
            oldPrimaryGuardian.setIsPrimary(false);
            guardianRepository.save(oldPrimaryGuardian);
        }

        // Set new primary
        newPrimary.setIsPrimary(true);
        Guardian updated = guardianRepository.save(newPrimary);

        log.info("Primary guardian updated successfully for student ID: {}", studentId);
        return updated;
    }

    /**
     * Remove a guardian
     * Ensures at least one guardian remains per student
     *
     * @param guardianId the guardian ID to remove
     * @throws NotFoundException if guardian not found
     * @throws BusinessException if attempting to remove the last guardian
     */
    public void removeGuardian(Long guardianId) {
        log.debug("Removing guardian ID: {}", guardianId);

        Guardian guardian = guardianRepository.findById(guardianId)
            .orElseThrow(() -> new NotFoundException("Guardian not found with ID: " + guardianId));

        // Check if this is the last guardian
        long guardianCount = guardianRepository.countByStudent(guardian.getStudent());
        if (guardianCount <= 1) {
            throw new BusinessException(
                "Cannot remove the last guardian. Student must have at least one guardian.");
        }

        // If removing primary guardian, set another guardian as primary
        if (guardian.getIsPrimary()) {
            List<Guardian> otherGuardians = guardianRepository.findByStudent(guardian.getStudent());
            Optional<Guardian> newPrimary = otherGuardians.stream()
                .filter(g -> !g.getId().equals(guardianId))
                .findFirst();

            if (newPrimary.isPresent()) {
                log.debug("Setting new primary guardian after removal");
                Guardian newPrimaryGuardian = newPrimary.get();
                newPrimaryGuardian.setIsPrimary(true);
                guardianRepository.save(newPrimaryGuardian);
            }
        }

        // Delete guardian
        guardianRepository.deleteById(guardianId);
        log.info("Guardian removed successfully: {}", guardianId);
    }

    /**
     * Get all guardians for a student
     *
     * @param studentId the student ID
     * @return list of guardians
     * @throws NotFoundException if student not found
     */
    @Transactional(readOnly = true)
    public List<Guardian> getGuardiansByStudentId(Long studentId) {
        log.debug("Getting guardians for student ID: {}", studentId);

        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentId));

        return guardianRepository.findByStudent(student);
    }

    /**
     * Get the primary guardian for a student
     *
     * @param studentId the student ID
     * @return optional primary guardian
     * @throws NotFoundException if student not found
     */
    @Transactional(readOnly = true)
    public Optional<Guardian> getPrimaryGuardian(Long studentId) {
        log.debug("Getting primary guardian for student ID: {}", studentId);

        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentId));

        return guardianRepository.findByStudentAndIsPrimary(student, true);
    }

    /**
     * Get a guardian by ID
     *
     * @param guardianId the guardian ID
     * @return the guardian
     * @throws NotFoundException if guardian not found
     */
    @Transactional(readOnly = true)
    public Guardian getGuardianById(Long guardianId) {
        log.debug("Getting guardian by ID: {}", guardianId);

        return guardianRepository.findById(guardianId)
            .orElseThrow(() -> new NotFoundException("Guardian not found with ID: " + guardianId));
    }
}
