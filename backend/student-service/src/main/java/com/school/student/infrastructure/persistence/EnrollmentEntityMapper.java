package com.school.student.infrastructure.persistence;

import com.school.student.domain.model.Enrollment;
import org.mapstruct.*;

/**
 * MapStruct mapper for Enrollment entity-domain conversion
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EnrollmentEntityMapper {

    /**
     * Convert domain model to JPA entity
     * Note: studentId needs manual handling in repository implementation
     */
    @Mapping(target = "student", ignore = true)
    EnrollmentEntity toEntity(Enrollment enrollment);

    /**
     * Convert JPA entity to domain model
     */
    @Mapping(target = "studentId", source = "student.id")
    Enrollment toDomain(EnrollmentEntity entity);
}
