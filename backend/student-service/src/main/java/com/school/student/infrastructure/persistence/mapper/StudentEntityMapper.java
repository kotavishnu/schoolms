package com.school.student.infrastructure.persistence.mapper;

import com.school.student.domain.model.Student;
import com.school.student.domain.model.StudentStatus;
import com.school.student.domain.model.valueobject.GuardianInfo;
import com.school.student.domain.model.valueobject.Mobile;
import com.school.student.infrastructure.persistence.entity.StudentEntity;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * MapStruct mapper for converting between Student domain model and StudentEntity JPA entity.
 *
 * <p>Mapping Strategy:
 * <ul>
 *   <li>Domain → Entity: Convert domain value objects to primitive types</li>
 *   <li>Entity → Domain: Reconstruct value objects from primitive types</li>
 *   <li>Handle null values gracefully</li>
 *   <li>Use custom methods for complex conversions</li>
 * </ul>
 *
 * <p>MapStruct generates implementation at compile time.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface StudentEntityMapper {

    /**
     * Convert domain Student to JPA StudentEntity.
     * Used when saving domain model to database.
     * Uses custom implementation due to private builder.
     *
     * @param student the domain model
     * @return StudentEntity for persistence
     */
    default StudentEntity toEntity(Student student) {
        if (student == null) {
            return null;
        }

        return StudentEntity.builder()
                .id(student.getId())
                .studentId(student.getStudentId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .dateOfBirth(student.getDateOfBirth())
                .mobile(student.getMobile() != null ? student.getMobile().getNumber() : null)
                .email(student.getEmail())
                .address(student.getAddress())
                .fathersName(student.getGuardianInfo() != null ? student.getGuardianInfo().getFathersName() : null)
                .mothersName(student.getGuardianInfo() != null ? student.getGuardianInfo().getMothersName() : null)
                .identificationMark(emptyToNull(student.getIdentificationMark()))
                .aadhaarNumber(emptyToNull(student.getAadhaarNumber()))
                .status(domainStatusToEntity(student.getStatus()))
                .version(student.getVersion())
                .createdAt(localDateTimeToOffsetDateTime(student.getCreatedAt()))
                .updatedAt(localDateTimeToOffsetDateTime(student.getUpdatedAt()))
                .createdBy(student.getCreatedBy())
                .updatedBy(student.getUpdatedBy())
                .build();
    }

    /**
     * Convert JPA StudentEntity to domain Student.
     * Used when loading from database to domain layer.
     * Uses setter methods to populate domain model.
     *
     * @param entity the JPA entity
     * @return Student domain model
     */
    default Student toDomain(StudentEntity entity) {
        if (entity == null) {
            return null;
        }

        // Create Student using register factory method with required fields
        Student student = Student.register(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getDateOfBirth(),
                entity.getMobile() != null ? Mobile.of(entity.getMobile()) : null,
                GuardianInfo.of(entity.getFathersName(), entity.getMothersName())
        );

        // Set persistence-managed and optional fields using setters
        student.setId(entity.getId());
        student.setStudentId(entity.getStudentId());
        student.setEmail(entity.getEmail());
        student.setAddress(entity.getAddress());
        student.setIdentificationMark(entity.getIdentificationMark());
        student.setAadhaarNumber(entity.getAadhaarNumber());

        // Handle status - deactivate if INACTIVE
        if (entity.getStatus() == StudentEntity.StudentStatusEnum.INACTIVE) {
            student.deactivate();
        }

        student.setVersion(entity.getVersion());
        student.setCreatedAt(offsetDateTimeToLocalDateTime(entity.getCreatedAt()));
        student.setUpdatedAt(offsetDateTimeToLocalDateTime(entity.getUpdatedAt()));
        student.setCreatedBy(entity.getCreatedBy());
        student.setUpdatedBy(entity.getUpdatedBy());

        return student;
    }

    // Custom mapping methods

    @Named("mobileToString")
    default String mobileToString(Mobile mobile) {
        return mobile != null ? mobile.getNumber() : null;
    }

    @Named("stringToMobile")
    default Mobile stringToMobile(String mobile) {
        return mobile != null ? Mobile.of(mobile) : null;
    }

    @Named("extractFathersName")
    default String extractFathersName(GuardianInfo guardianInfo) {
        return guardianInfo != null ? guardianInfo.getFathersName() : null;
    }

    @Named("extractMothersName")
    default String extractMothersName(GuardianInfo guardianInfo) {
        return guardianInfo != null ? guardianInfo.getMothersName() : null;
    }

    @Named("buildGuardianInfo")
    default GuardianInfo buildGuardianInfo(StudentEntity entity) {
        if (entity == null) {
            return null;
        }
        return GuardianInfo.of(entity.getFathersName(), entity.getMothersName());
    }

    @Named("domainStatusToEntity")
    default StudentEntity.StudentStatusEnum domainStatusToEntity(StudentStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case ACTIVE -> StudentEntity.StudentStatusEnum.ACTIVE;
            case INACTIVE -> StudentEntity.StudentStatusEnum.INACTIVE;
        };
    }

    @Named("entityStatusToDomain")
    default StudentStatus entityStatusToDomain(StudentEntity.StudentStatusEnum status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case ACTIVE -> StudentStatus.ACTIVE;
            case INACTIVE -> StudentStatus.INACTIVE;
        };
    }

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(java.time.LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    @Named("offsetDateTimeToLocalDateTime")
    default java.time.LocalDateTime offsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.toLocalDateTime();
    }

    /**
     * Converts empty strings to NULL to satisfy database constraints.
     * Database constraints require NULL instead of empty strings for optional fields.
     *
     * @param value the string value
     * @return null if string is null or blank, otherwise the trimmed value
     */
    @Named("emptyToNull")
    default String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
