package com.school.management.service;

import com.school.management.dto.request.ClassRequestDTO;
import com.school.management.dto.response.ClassResponseDTO;
import com.school.management.exception.DuplicateResourceException;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.exception.ValidationException;
import com.school.management.mapper.ClassMapper;
import com.school.management.model.SchoolClass;
import com.school.management.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Class management operations.
 *
 * Handles business logic for class CRUD, enrollment tracking, and capacity management.
 *
 * @author School Management Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ClassService {

    private final ClassRepository classRepository;
    private final ClassMapper classMapper;

    /**
     * Create a new class
     *
     * @param requestDTO Class data
     * @return Created class DTO
     * @throws DuplicateResourceException if class with same number, section, and year exists
     */
    @Transactional
    public ClassResponseDTO createClass(ClassRequestDTO requestDTO) {
        log.info("Creating class: {} - {}, Academic Year: {}",
                requestDTO.getClassNumber(), requestDTO.getSection(), requestDTO.getAcademicYear());

        // Validate uniqueness (class number + section + academic year)
        if (classRepository.existsByClassNumberAndSectionAndAcademicYear(
                requestDTO.getClassNumber(),
                requestDTO.getSection(),
                requestDTO.getAcademicYear())) {
            throw new DuplicateResourceException(
                    "SchoolClass",
                    "classNumber-section-academicYear",
                    String.format("%d-%s-%s", requestDTO.getClassNumber(),
                            requestDTO.getSection(), requestDTO.getAcademicYear()));
        }

        // Map DTO to entity
        SchoolClass schoolClass = classMapper.toEntity(requestDTO);
        schoolClass.setCurrentEnrollment(0); // Initialize enrollment to 0

        // Save class
        SchoolClass savedClass = classRepository.save(schoolClass);

        log.info("Class created successfully with ID: {}", savedClass.getId());

        return classMapper.toResponseDTO(savedClass);
    }

    /**
     * Get class by ID
     *
     * @param id Class ID
     * @return Class DTO
     * @throws ResourceNotFoundException if class not found
     */
    public ClassResponseDTO getClass(Long id) {
        log.debug("Fetching class with ID: {}", id);

        SchoolClass schoolClass = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolClass", "id", id));

        return classMapper.toResponseDTO(schoolClass);
    }

    /**
     * Get all classes
     *
     * @return List of class DTOs
     */
    public List<ClassResponseDTO> getAllClasses() {
        log.debug("Fetching all classes");

        List<SchoolClass> classes = classRepository.findAll();

        return classMapper.toResponseDTOList(classes);
    }

    /**
     * Get classes by academic year (ordered)
     *
     * @param academicYear Academic year (e.g., "2024-2025")
     * @return Ordered list of class DTOs
     */
    public List<ClassResponseDTO> getClassesByAcademicYear(String academicYear) {
        log.debug("Fetching classes for academic year: {}", academicYear);

        List<SchoolClass> classes = classRepository.findAllByAcademicYearOrdered(academicYear);

        return classMapper.toResponseDTOList(classes);
    }

    /**
     * Get classes by class number (all sections)
     *
     * @param classNumber Class number (1-10)
     * @param academicYear Academic year
     * @return List of class DTOs for different sections
     */
    public List<ClassResponseDTO> getClassesByNumber(Integer classNumber, String academicYear) {
        log.debug("Fetching classes for class number {} in year {}", classNumber, academicYear);

        List<SchoolClass> classes = classRepository.findByClassNumberAndAcademicYear(classNumber, academicYear);

        return classMapper.toResponseDTOList(classes);
    }

    /**
     * Get classes with available seats
     *
     * @param academicYear Academic year
     * @return List of classes with available capacity
     */
    public List<ClassResponseDTO> getClassesWithAvailableSeats(String academicYear) {
        log.debug("Fetching classes with available seats for year: {}", academicYear);

        List<SchoolClass> classes = classRepository.findClassesWithAvailableSeats(academicYear);

        return classMapper.toResponseDTOList(classes);
    }

    /**
     * Get classes that are almost full (>80% capacity)
     *
     * @param academicYear Academic year
     * @return List of almost full classes
     */
    public List<ClassResponseDTO> getAlmostFullClasses(String academicYear) {
        log.debug("Fetching almost full classes for year: {}", academicYear);

        List<SchoolClass> classes = classRepository.findAlmostFullClasses(academicYear);

        return classMapper.toResponseDTOList(classes);
    }

    /**
     * Update existing class
     *
     * @param id Class ID
     * @param requestDTO Updated class data
     * @return Updated class DTO
     * @throws ResourceNotFoundException if class not found
     * @throws DuplicateResourceException if updated values conflict with existing class
     * @throws ValidationException if new capacity is less than current enrollment
     */
    @Transactional
    public ClassResponseDTO updateClass(Long id, ClassRequestDTO requestDTO) {
        log.info("Updating class with ID: {}", id);

        // Fetch existing class
        SchoolClass schoolClass = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolClass", "id", id));

        // Validate capacity
        if (requestDTO.getCapacity() < schoolClass.getCurrentEnrollment()) {
            throw new ValidationException(
                    String.format("New capacity (%d) cannot be less than current enrollment (%d)",
                            requestDTO.getCapacity(), schoolClass.getCurrentEnrollment()));
        }

        // Check for duplicate if class number/section/year changed
        boolean isChanged = !schoolClass.getClassNumber().equals(requestDTO.getClassNumber()) ||
                           !schoolClass.getSection().equals(requestDTO.getSection()) ||
                           !schoolClass.getAcademicYear().equals(requestDTO.getAcademicYear());

        if (isChanged) {
            classRepository.findByClassNumberAndSectionAndAcademicYear(
                    requestDTO.getClassNumber(),
                    requestDTO.getSection(),
                    requestDTO.getAcademicYear())
                    .filter(c -> !c.getId().equals(id))
                    .ifPresent(c -> {
                        throw new DuplicateResourceException(
                                "SchoolClass",
                                "classNumber-section-academicYear",
                                String.format("%d-%s-%s", requestDTO.getClassNumber(),
                                        requestDTO.getSection(), requestDTO.getAcademicYear()));
                    });
        }

        // Update fields
        classMapper.updateEntityFromDTO(requestDTO, schoolClass);

        SchoolClass updatedClass = classRepository.save(schoolClass);

        log.info("Class updated successfully with ID: {}", updatedClass.getId());

        return classMapper.toResponseDTO(updatedClass);
    }

    /**
     * Delete class
     *
     * @param id Class ID
     * @throws ResourceNotFoundException if class not found
     * @throws ValidationException if class has enrolled students
     */
    @Transactional
    public void deleteClass(Long id) {
        log.info("Deleting class with ID: {}", id);

        SchoolClass schoolClass = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolClass", "id", id));

        // Check if class has students
        if (schoolClass.getCurrentEnrollment() != null && schoolClass.getCurrentEnrollment() > 0) {
            throw new ValidationException(
                    String.format("Cannot delete class %s with %d enrolled students",
                            schoolClass.getDisplayName(), schoolClass.getCurrentEnrollment()));
        }

        classRepository.deleteById(id);

        log.info("Class deleted successfully with ID: {}", id);
    }

    /**
     * Get total students count for academic year
     *
     * @param academicYear Academic year
     * @return Total student count
     */
    public Long getTotalStudents(String academicYear) {
        log.debug("Counting total students for year: {}", academicYear);

        Long total = classRepository.countTotalStudents(academicYear);

        return total != null ? total : 0L;
    }

    /**
     * Check if class exists
     *
     * @param classNumber Class number
     * @param section Section
     * @param academicYear Academic year
     * @return true if class exists
     */
    public boolean classExists(Integer classNumber, String section, String academicYear) {
        return classRepository.existsByClassNumberAndSectionAndAcademicYear(
                classNumber, section, academicYear);
    }
}
