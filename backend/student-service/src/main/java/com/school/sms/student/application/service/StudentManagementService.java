package com.school.sms.student.application.service;

import com.school.sms.student.application.dto.StudentDTO;
import com.school.sms.student.application.mapper.StudentMapper;
import com.school.sms.student.domain.entity.Student;
import com.school.sms.student.domain.exception.DuplicateMobileException;
import com.school.sms.student.domain.exception.InvalidAgeException;
import com.school.sms.student.domain.exception.StudentNotFoundException;
import com.school.sms.student.domain.repository.StudentRepository;
import com.school.sms.student.presentation.dto.UpdateStudentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

/**
 * Service for student management operations.
 * Handles student updates and profile management.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StudentManagementService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    private static final int MIN_AGE = 3;
    private static final int MAX_AGE = 18;

    /**
     * Updates an existing student's information.
     *
     * @param studentId the student ID
     * @param request the update request
     * @param userId the user performing the operation
     * @return updated StudentDTO
     * @throws StudentNotFoundException if student not found
     * @throws InvalidAgeException if age is not between 3-18 years
     * @throws DuplicateMobileException if mobile number already exists
     * @throws ObjectOptimisticLockingFailureException if concurrent update detected
     */
    public StudentDTO updateStudent(String studentId, UpdateStudentRequest request, String userId) {
        log.info("Updating student: {}", studentId);

        // Find student
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found: " + studentId));

        // Validate version for optimistic locking
        if (!student.getVersion().equals(request.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Student.class, studentId);
        }

        // Validate age if date of birth changed
        if (!student.getDateOfBirth().equals(request.getDateOfBirth())) {
            validateAge(request.getDateOfBirth());
        }

        // Check mobile uniqueness if mobile changed
        if (!student.getMobile().equals(request.getMobile())) {
            if (studentRepository.existsByMobileAndStudentIdNot(request.getMobile(), studentId)) {
                throw new DuplicateMobileException(
                    "Mobile number already registered: " + request.getMobile());
            }
        }

        // Update entity
        studentMapper.updateEntityFromRequest(request, student);
        student.setUpdatedBy(userId);

        // Save
        Student updated = studentRepository.save(student);

        log.info("Student updated successfully: {}", studentId);
        return studentMapper.toDTO(updated);
    }

    /**
     * Deletes a student (soft delete by setting status to INACTIVE).
     *
     * @param studentId the student ID
     * @throws StudentNotFoundException if student not found
     */
    public void deleteStudent(String studentId) {
        log.info("Deleting student: {}", studentId);

        // Verify student exists
        if (!studentRepository.findById(studentId).isPresent()) {
            throw new StudentNotFoundException("Student not found: " + studentId);
        }

        // Soft delete
        studentRepository.deleteById(studentId);

        log.info("Student deleted successfully: {}", studentId);
    }

    private void validateAge(LocalDate dateOfBirth) {
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age < MIN_AGE || age > MAX_AGE) {
            throw new InvalidAgeException(
                String.format("Student age must be between %d and %d years. Provided age: %d",
                    MIN_AGE, MAX_AGE, age));
        }
    }
}
