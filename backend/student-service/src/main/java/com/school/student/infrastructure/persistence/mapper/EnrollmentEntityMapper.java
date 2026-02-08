package com.school.student.infrastructure.persistence.mapper;

import com.school.student.domain.model.Enrollment;
import com.school.student.domain.model.EnrollmentStatus;
import com.school.student.infrastructure.persistence.entity.EnrollmentEntity;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * MapStruct mapper for converting between Enrollment domain model and EnrollmentEntity JPA entity.
 *
 * <p>Mapping Strategy:
 * <ul>
 *   <li>Domain → Entity: Convert domain to entity structure</li>
 *   <li>Entity → Domain: Reconstruct domain from entity</li>
 *   <li>Handle StudentEntity reference separately (set by repository)</li>
 *   <li>Use custom methods for status enum conversion</li>
 * </ul>
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EnrollmentEntityMapper {

    /**
     * Convert domain Enrollment to JPA EnrollmentEntity.
     * Note: StudentEntity reference must be set separately by repository.
     * Uses custom implementation due to private builder.
     *
     * @param enrollment the domain model
     * @return EnrollmentEntity for persistence
     */
    default EnrollmentEntity toEntity(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }

        return EnrollmentEntity.builder()
                .id(enrollment.getId())
                .academicYear(enrollment.getAcademicYear())
                .gradeClass(enrollment.getGradeClass())
                .section(enrollment.getSection())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .withdrawalDate(enrollment.getWithdrawalDate())
                .status(domainStatusToEntity(enrollment.getStatus()))
                .remarks(enrollment.getRemarks())
                .version(enrollment.getVersion())
                .createdAt(localDateToOffsetDateTime(enrollment.getCreatedAt()))
                .build();
        // Note: student field set by adapter
    }

    /**
     * Convert JPA EnrollmentEntity to domain Enrollment.
     * Uses setter methods to populate domain model.
     *
     * @param entity the JPA entity
     * @return Enrollment domain model
     */
    default Enrollment toDomain(EnrollmentEntity entity) {
        if (entity == null) {
            return null;
        }

        // Create Enrollment using enroll factory method
        Enrollment enrollment = Enrollment.enroll(
                entity.getStudent().getId(),
                entity.getAcademicYear(),
                entity.getGradeClass(),
                entity.getSection(),
                entity.getEnrollmentDate(),
                entity.getRemarks()
        );

        // Set persistence-managed fields using setters
        enrollment.setId(entity.getId());
        enrollment.setWithdrawalDate(entity.getWithdrawalDate());
        enrollment.setStatus(entityStatusToDomain(entity.getStatus()));
        enrollment.setVersion(entity.getVersion());
        enrollment.setCreatedAt(offsetDateTimeToLocalDate(entity.getCreatedAt()));

        return enrollment;
    }

    // Custom mapping methods

    @Named("domainStatusToEntity")
    default EnrollmentEntity.EnrollmentStatusEnum domainStatusToEntity(EnrollmentStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case ACTIVE -> EnrollmentEntity.EnrollmentStatusEnum.ACTIVE;
            case WITHDRAWN -> EnrollmentEntity.EnrollmentStatusEnum.WITHDRAWN;
            case COMPLETED -> EnrollmentEntity.EnrollmentStatusEnum.COMPLETED;
        };
    }

    @Named("entityStatusToDomain")
    default EnrollmentStatus entityStatusToDomain(EnrollmentEntity.EnrollmentStatusEnum status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case ACTIVE -> EnrollmentStatus.ACTIVE;
            case WITHDRAWN -> EnrollmentStatus.WITHDRAWN;
            case COMPLETED -> EnrollmentStatus.COMPLETED;
        };
    }

    @Named("localDateToOffsetDateTime")
    default OffsetDateTime localDateToOffsetDateTime(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
    }

    @Named("offsetDateTimeToLocalDate")
    default LocalDate offsetDateTimeToLocalDate(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.toLocalDate();
    }
}
