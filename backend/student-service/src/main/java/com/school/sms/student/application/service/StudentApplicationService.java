package com.school.sms.student.application.service;

import com.school.sms.student.domain.exception.DuplicateAadhaarException;
import com.school.sms.student.domain.exception.DuplicateMobileException;
import com.school.sms.student.domain.exception.StudentNotFoundException;
import com.school.sms.student.domain.model.Student;
import com.school.sms.student.domain.model.StudentId;
import com.school.sms.student.domain.model.StudentStatus;
import com.school.sms.student.domain.repository.StudentRepository;
import com.school.sms.student.presentation.dto.request.CreateStudentRequest;
import com.school.sms.student.presentation.dto.request.UpdateStudentRequest;
import com.school.sms.student.presentation.dto.response.PagedStudentResponse;
import com.school.sms.student.presentation.dto.response.StudentResponse;
import com.school.sms.student.presentation.mapper.StudentDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for student-related use cases.
 *
 * <p>This service orchestrates business operations, validates business rules,
 * and coordinates between the domain and presentation layers.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentApplicationService {

    private final StudentRepository studentRepository;
    private final StudentDtoMapper dtoMapper;

    /**
     * Creates a new student.
     *
     * <p>Validates:</p>
     * <ul>
     *   <li>Mobile number is unique</li>
     *   <li>Aadhaar number is unique (if provided)</li>
     *   <li>Age is between 3 and 18 years (validated in domain layer)</li>
     * </ul>
     *
     * @param request the create student request
     * @return the created student response
     * @throws DuplicateMobileException if mobile already exists
     * @throws DuplicateAadhaarException if Aadhaar already exists
     */
    public StudentResponse createStudent(CreateStudentRequest request) {
        log.info("Creating new student with mobile: {}", request.getMobile());

        // Validate unique mobile
        if (studentRepository.existsByMobile(request.getMobile())) {
            log.warn("Attempt to create student with duplicate mobile: {}", request.getMobile());
            throw new DuplicateMobileException(request.getMobile());
        }

        // Validate unique Aadhaar (if provided)
        if (request.getAadhaarNumber() != null && !request.getAadhaarNumber().isBlank()) {
            if (studentRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
                log.warn("Attempt to create student with duplicate Aadhaar: {}", request.getAadhaarNumber());
                throw new DuplicateAadhaarException(request.getAadhaarNumber());
            }
        }

        // Generate student ID (will be set by repository)
        String currentUser = getCurrentUser();
        Student student = dtoMapper.toDomain(request, null, currentUser);

        // Save student (repository will generate ID)
        Student savedStudent = studentRepository.save(student);

        log.info("Student created successfully with ID: {}", savedStudent.getStudentId());
        return dtoMapper.toResponse(savedStudent);
    }

    /**
     * Retrieves a student by their student ID.
     *
     * @param studentId the student ID
     * @return the student response
     * @throws StudentNotFoundException if student not found
     */
    @Transactional(readOnly = true)
    public StudentResponse getStudent(String studentId) {
        log.debug("Retrieving student: {}", studentId);

        Student student = studentRepository.findByStudentId(StudentId.of(studentId))
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        return dtoMapper.toResponse(student);
    }

    /**
     * Updates an existing student.
     *
     * <p>Only allows updating: firstName, lastName, mobile, status</p>
     * <p>Validates version for optimistic locking.</p>
     *
     * @param studentId the student ID
     * @param request the update request
     * @return the updated student response
     * @throws StudentNotFoundException if student not found
     * @throws DuplicateMobileException if new mobile already exists for another student
     */
    public StudentResponse updateStudent(String studentId, UpdateStudentRequest request) {
        log.info("Updating student: {}", studentId);

        // Find existing student
        Student student = studentRepository.findByStudentId(StudentId.of(studentId))
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        // Validate mobile uniqueness (if changed)
        if (!student.getContactInfo().getMobile().equals(request.getMobile())) {
            if (studentRepository.existsByMobile(request.getMobile())) {
                log.warn("Attempt to update student with duplicate mobile: {}", request.getMobile());
                throw new DuplicateMobileException(request.getMobile());
            }
        }

        String currentUser = getCurrentUser();

        // Update personal info (if changed)
        if (!student.getPersonalInfo().getFirstName().equals(request.getFirstName()) ||
            !student.getPersonalInfo().getLastName().equals(request.getLastName())) {
            student = student.updatePersonalInfo(request.getFirstName(), request.getLastName(), currentUser);
        }

        // Update contact info (if changed)
        if (!student.getContactInfo().getMobile().equals(request.getMobile())) {
            student = student.updateContactInfo(request.getMobile(), currentUser);
        }

        // Update status (if changed)
        StudentStatus newStatus = StudentStatus.valueOf(request.getStatus());
        if (student.getStatus() != newStatus) {
            student = newStatus == StudentStatus.ACTIVE
                ? student.activate(currentUser)
                : student.deactivate(currentUser);
        }

        // Save updated student
        Student updatedStudent = studentRepository.save(student);

        log.info("Student updated successfully: {}", studentId);
        return dtoMapper.toResponse(updatedStudent);
    }

    /**
     * Deletes a student.
     *
     * @param studentId the student ID
     * @throws StudentNotFoundException if student not found
     */
    public void deleteStudent(String studentId) {
        log.info("Deleting student: {}", studentId);

        Student student = studentRepository.findByStudentId(StudentId.of(studentId))
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        studentRepository.deleteById(student.getId());

        log.info("Student deleted successfully: {}", studentId);
    }

    /**
     * Searches for students with pagination.
     *
     * <p>Search criteria (all optional):</p>
     * <ul>
     *   <li>lastName - Partial, case-insensitive match</li>
     *   <li>fathersName - Partial, case-insensitive match</li>
     *   <li>status - Exact match (ACTIVE or INACTIVE)</li>
     * </ul>
     *
     * <p>If no criteria provided, returns all students.</p>
     *
     * @param lastName the last name to search for (optional)
     * @param fathersName the father's name to search for (optional)
     * @param status the student status (optional)
     * @param pageable pagination information
     * @return a paged response of students
     */
    @Transactional(readOnly = true)
    public PagedStudentResponse searchStudents(
        String lastName,
        String fathersName,
        String status,
        Pageable pageable
    ) {
        log.debug("Searching students - lastName: {}, fathersName: {}, status: {}",
                  lastName, fathersName, status);

        Page<Student> page;

        if (lastName != null && !lastName.isBlank()) {
            page = studentRepository.findByLastName(lastName, pageable);
        } else if (fathersName != null && !fathersName.isBlank()) {
            page = studentRepository.findByFathersName(fathersName, pageable);
        } else if (status != null && !status.isBlank()) {
            StudentStatus studentStatus = StudentStatus.valueOf(status.toUpperCase());
            page = studentRepository.findByStatus(studentStatus, pageable);
        } else {
            page = studentRepository.findAll(pageable);
        }

        log.debug("Found {} students (page {} of {})",
                  page.getNumberOfElements(), page.getNumber() + 1, page.getTotalPages());

        return dtoMapper.toPagedResponse(page);
    }

    /**
     * Gets the current user from security context.
     * For now, returns a default value (authentication will be added later).
     *
     * @return the current username
     */
    private String getCurrentUser() {
        // TODO: Extract from SecurityContext when authentication is implemented
        return "system";
    }
}
