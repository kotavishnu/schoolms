package com.school.student.service.mapper;

import com.school.student.controller.dto.request.EnrollmentRequest;
import com.school.student.controller.dto.response.EnrollmentResponse;
import com.school.student.domain.model.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Enrollment DTO-Domain conversions
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EnrollmentMapper {

    /**
     * Convert request DTO to domain model
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "withdrawalDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Enrollment toDomain(EnrollmentRequest request);

    /**
     * Convert domain model to response DTO
     */
    EnrollmentResponse toResponse(Enrollment enrollment);
}
