package com.school.management.service;

import com.school.management.dto.SchoolClassDTO;
import com.school.management.exception.BadRequestException;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.model.SchoolClass;
import com.school.management.repository.SchoolClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SchoolClassService {

    private final SchoolClassRepository classRepository;

    public SchoolClassDTO createClass(SchoolClassDTO dto) {
        // Check for duplicate
        if (classRepository.findByClassNumberAndSectionAndAcademicYear(
                dto.getClassNumber(), dto.getSection(), dto.getAcademicYear()).isPresent()) {
            throw new BadRequestException("Class already exists for this academic year");
        }

        SchoolClass schoolClass = SchoolClass.builder()
                .classNumber(dto.getClassNumber())
                .section(dto.getSection())
                .academicYear(dto.getAcademicYear())
                .capacity(dto.getCapacity())
                .currentStrength(0)
                .build();

        SchoolClass saved = classRepository.save(schoolClass);
        return convertToDTO(saved);
    }

    @Transactional(readOnly = true)
    public SchoolClassDTO getClassById(Long id) {
        SchoolClass schoolClass = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", id));
        return convertToDTO(schoolClass);
    }

    @Transactional(readOnly = true)
    public List<SchoolClassDTO> getAllClasses() {
        return classRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SchoolClassDTO> getClassesByAcademicYear(String academicYear) {
        return classRepository.findByAcademicYear(academicYear).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SchoolClassDTO updateClass(Long id, SchoolClassDTO dto) {
        SchoolClass existingClass = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", id));

        existingClass.setClassNumber(dto.getClassNumber());
        existingClass.setSection(dto.getSection());
        existingClass.setAcademicYear(dto.getAcademicYear());
        existingClass.setCapacity(dto.getCapacity());

        SchoolClass updated = classRepository.save(existingClass);
        return convertToDTO(updated);
    }

    public void deleteClass(Long id) {
        SchoolClass schoolClass = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", id));

        if (schoolClass.getCurrentStrength() > 0) {
            throw new BadRequestException("Cannot delete class with enrolled students");
        }

        classRepository.delete(schoolClass);
    }

    private SchoolClassDTO convertToDTO(SchoolClass schoolClass) {
        return SchoolClassDTO.builder()
                .id(schoolClass.getId())
                .classNumber(schoolClass.getClassNumber())
                .section(schoolClass.getSection())
                .academicYear(schoolClass.getAcademicYear())
                .capacity(schoolClass.getCapacity())
                .currentStrength(schoolClass.getCurrentStrength())
                .build();
    }
}
