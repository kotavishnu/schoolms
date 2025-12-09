package com.sms.student.application.mapper;

import com.sms.shared.util.DateTimeUtils;
import com.sms.student.domain.model.Student;
import com.sms.student.infrastructure.persistence.entity.StudentEntity;
import com.sms.student.presentation.dto.CreateStudentRequest;
import com.sms.student.presentation.dto.StudentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Student domain model and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class StudentMapper {

    /**
     * Convert StudentEntity to Student domain model.
     */
    public abstract Student entityToDomain(StudentEntity entity);

    /**
     * Convert Student domain model to StudentEntity.
     */
    public abstract StudentEntity domainToEntity(Student domain);

    /**
     * Convert Student domain model to StudentDTO.
     * Includes age calculation.
     */
    public StudentDTO domainToDTO(Student domain) {
        if (domain == null) {
            return null;
        }

        Integer age = domain.getDateOfBirth() != null
            ? DateTimeUtils.calculateAge(domain.getDateOfBirth())
            : null;

        return new StudentDTO(
            domain.getStudentId(),
            domain.getStudentKey(),
            domain.getFirstName(),
            domain.getLastName(),
            domain.getDateOfBirth(),
            age,
            domain.getMobile(),
            domain.getEmail(),
            domain.getAddress(),
            domain.getFatherNameOrGuardian(),
            domain.getMotherName(),
            domain.getIdentificationMark(),
            domain.getAdhaarNumber(),
            domain.getStatus(),
            domain.getCreatedAt(),
            domain.getUpdatedAt()
        );
    }

    /**
     * Convert StudentEntity to StudentDTO.
     * Includes age calculation.
     */
    public StudentDTO entityToDTO(StudentEntity entity) {
        if (entity == null) {
            return null;
        }

        Integer age = entity.getDateOfBirth() != null
            ? DateTimeUtils.calculateAge(entity.getDateOfBirth())
            : null;

        return new StudentDTO(
            entity.getStudentId(),
            entity.getStudentKey(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getDateOfBirth(),
            age,
            entity.getMobile(),
            entity.getEmail(),
            entity.getAddress(),
            entity.getFatherNameOrGuardian(),
            entity.getMotherName(),
            entity.getIdentificationMark(),
            entity.getAdhaarNumber(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Convert CreateStudentRequest to Student domain model.
     */
    public abstract Student requestToDomain(CreateStudentRequest request);
}
