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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service layer for Student management operations.
 *
 * Handles business logic for student registration, updates, search, and validation.
 *
 * @author School Management Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final StudentMapper studentMapper;

    /**
     * Create a new student
     *
     * @param requestDTO Student registration data
     * @return Created student DTO
     * @throws DuplicateResourceException if mobile number already exists
     * @throws ResourceNotFoundException if class not found
     * @throws ValidationException if class capacity exceeded
     */
    @Transactional
    public StudentResponseDTO createStudent(StudentRequestDTO requestDTO) {
        log.info("Creating student: {} {}", requestDTO.getFirstName(), requestDTO.getLastName());

        // Validate mobile number uniqueness
        if (studentRepository.existsByMobile(requestDTO.getMobile())) {
            throw new DuplicateResourceException("Student", "mobile", requestDTO.getMobile());
        }

        // Fetch and validate class
        SchoolClass schoolClass = classRepository.findById(requestDTO.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("SchoolClass", "id", requestDTO.getClassId()));

        // Check class capacity
        long currentStudents = studentRepository.countBySchoolClassId(requestDTO.getClassId());
        if (currentStudents >= schoolClass.getCapacity()) {
            throw new ValidationException(
                    String.format("Class %s capacity exceeded. Current: %d, Capacity: %d",
                                  schoolClass.getDisplayName(), currentStudents, schoolClass.getCapacity()));
        }

        // Map DTO to entity
        Student student = studentMapper.toEntity(requestDTO);
        student.setSchoolClass(schoolClass);
        student.setEnrollmentDate(LocalDate.now());
        student.setStatus(StudentStatus.ACTIVE);

        // Save student
        Student savedStudent = studentRepository.save(student);

        // Update class enrollment count
        schoolClass.setCurrentEnrollment((int) currentStudents + 1);
        classRepository.save(schoolClass);

        log.info("Student created successfully with ID: {}", savedStudent.getId());

        return studentMapper.toResponseDTO(savedStudent);
    }

    /**
     * Get student by ID
     *
     * @param id Student ID
     * @return Student DTO
     * @throws ResourceNotFoundException if student not found
     */
    public StudentResponseDTO getStudent(Long id) {
        log.debug("Fetching student with ID: {}", id);

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        return studentMapper.toResponseDTO(student);
    }

    /**
     * Get all students or filter by class
     *
     * @param classId Optional class ID filter
     * @return List of student DTOs
     */
    public List<StudentResponseDTO> getAllStudents(Long classId) {
        log.debug("Fetching all students. ClassId filter: {}", classId);

        List<Student> students = classId != null
                ? studentRepository.findBySchoolClassId(classId)
                : studentRepository.findAll();

        return studentMapper.toResponseDTOList(students);
    }

    /**
     * Search students by name (first or last name) - Case insensitive
     *
     * @param query Search query
     * @return List of matching students
     */
    public List<StudentResponseDTO> searchStudents(String query) {
        log.debug("Searching students with query: {}", query);

        if (query == null || query.trim().length() < 2) {
            throw new ValidationException("Search query must be at least 2 characters");
        }

        List<Student> students = studentRepository.searchByName(query.trim());

        return studentMapper.toResponseDTOList(students);
    }

    /**
     * Search students for autocomplete (name or mobile)
     *
     * @param query Search query
     * @return List of matching students (limited results)
     */
    public List<StudentResponseDTO> searchForAutocomplete(String query) {
        log.debug("Autocomplete search with query: {}", query);

        if (query == null || query.trim().length() < 2) {
            return List.of();
        }

        List<Student> students = studentRepository.searchForAutocomplete(query.trim());

        return studentMapper.toResponseDTOList(students);
    }

    /**
     * Update existing student
     *
     * @param id Student ID
     * @param requestDTO Updated student data
     * @return Updated student DTO
     * @throws ResourceNotFoundException if student not found
     * @throws DuplicateResourceException if mobile number already in use by another student
     */
    @Transactional
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO requestDTO) {
        log.info("Updating student with ID: {}", id);

        // Fetch existing student
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        // Validate mobile uniqueness (excluding current student)
        studentRepository.findByMobile(requestDTO.getMobile())
                .filter(s -> !s.getId().equals(id))
                .ifPresent(s -> {
                    throw new DuplicateResourceException("Student", "mobile", requestDTO.getMobile());
                });

        // Update class if changed
        if (!student.getSchoolClass().getId().equals(requestDTO.getClassId())) {
            SchoolClass newClass = classRepository.findById(requestDTO.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("SchoolClass", "id", requestDTO.getClassId()));

            // Check new class capacity
            long currentStudents = studentRepository.countBySchoolClassId(requestDTO.getClassId());
            if (currentStudents >= newClass.getCapacity()) {
                throw new ValidationException(
                        String.format("Class %s capacity exceeded", newClass.getDisplayName()));
            }

            // Update enrollment counts
            SchoolClass oldClass = student.getSchoolClass();
            oldClass.setCurrentEnrollment(oldClass.getCurrentEnrollment() - 1);
            classRepository.save(oldClass);

            newClass.setCurrentEnrollment((int) currentStudents + 1);
            classRepository.save(newClass);

            student.setSchoolClass(newClass);
        }

        // Update other fields
        studentMapper.updateEntityFromDTO(requestDTO, student);

        Student updatedStudent = studentRepository.save(student);

        log.info("Student updated successfully with ID: {}", updatedStudent.getId());

        return studentMapper.toResponseDTO(updatedStudent);
    }

    /**
     * Delete student (soft delete by marking as INACTIVE)
     *
     * @param id Student ID
     * @throws ResourceNotFoundException if student not found
     */
    @Transactional
    public void deleteStudent(Long id) {
        log.info("Deleting student with ID: {}", id);

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        // Update class enrollment count
        SchoolClass schoolClass = student.getSchoolClass();
        if (schoolClass.getCurrentEnrollment() > 0) {
            schoolClass.setCurrentEnrollment(schoolClass.getCurrentEnrollment() - 1);
            classRepository.save(schoolClass);
        }

        // Perform hard delete (or change to soft delete by setting status = INACTIVE)
        studentRepository.deleteById(id);

        log.info("Student deleted successfully with ID: {}", id);
    }

    /**
     * Get students with pending fee payments
     *
     * @return List of students with pending fees
     */
    public List<StudentResponseDTO> getStudentsWithPendingFees() {
        log.debug("Fetching students with pending fees");

        List<Student> students = studentRepository.findStudentsWithPendingFees();

        return studentMapper.toResponseDTOList(students);
    }
}
