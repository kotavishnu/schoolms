package com.school.student.service.mapper;

import com.school.student.controller.dto.request.StudentRequest;
import com.school.student.controller.dto.request.StudentUpdateRequest;
import com.school.student.controller.dto.response.StudentResponse;
import com.school.student.domain.model.Student;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.Period;

/**
 * MapStruct mapper for Student DTO-Domain conversions
 * Generates implementation at compile time
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    /**
     * Convert request DTO to domain model
     * Ignores generated fields (id, studentId, status, version, timestamps)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Student toDomain(StudentRequest request);

    /**
     * Convert domain model to response DTO
     * Calculates age from date of birth
     */
    @Mapping(target = "age", expression = "java(calculateAge(student.getDateOfBirth()))")
    StudentResponse toResponse(Student student);

    /**
     * Update existing domain model from update request
     * Only updates editable fields
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "fathersName", ignore = true)
    @Mapping(target = "mothersName", ignore = true)
    @Mapping(target = "identificationMark", ignore = true)
    @Mapping(target = "aadhaarNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateDomain(@MappingTarget Student student, StudentUpdateRequest request);

    /**
     * Calculate age from date of birth
     */
    default int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
