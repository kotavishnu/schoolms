package com.school.student.mapper;

import com.school.student.domain.entity.Enrollment;
import com.school.student.domain.entity.EnrollmentStatus;
import com.school.student.dto.request.EnrollmentRequest;
import com.school.student.dto.response.EnrollmentResponse;
import org.mapstruct.*;

/**
 * Enrollment Mapper
 *
 * MapStruct mapper for converting between Enrollment entity and DTOs.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EnrollmentMapper {

    /**
     * Map EnrollmentRequest to Enrollment entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true) // Set manually in service
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "withdrawalDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Enrollment toEntity(EnrollmentRequest request);

    /**
     * Map Enrollment entity to EnrollmentResponse
     */
    @Mapping(source = "student.studentId", target = "studentId")
    EnrollmentResponse toResponse(Enrollment entity);
}
