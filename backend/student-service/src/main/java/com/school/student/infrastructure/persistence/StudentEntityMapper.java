package com.school.student.infrastructure.persistence;

import com.school.student.domain.model.Student;
import org.mapstruct.*;

/**
 * MapStruct mapper for converting between JPA entities and domain models
 * Keeps domain layer clean from JPA annotations
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentEntityMapper {

    /**
     * Convert domain model to JPA entity
     */
    @Mapping(target = "enrollments", ignore = true)
    StudentEntity toEntity(Student student);

    /**
     * Convert JPA entity to domain model
     */
    Student toDomain(StudentEntity entity);
}
