package com.school.sms.student.infrastructure.persistence.mapper;

import com.school.sms.student.domain.model.AuditInfo;
import com.school.sms.student.domain.model.Enrollment;
import com.school.sms.student.domain.model.EnrollmentStatus;
import com.school.sms.student.infrastructure.persistence.entity.EnrollmentJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between Enrollment domain entity and EnrollmentJpaEntity.
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Mapper(componentModel = "spring")
public interface EnrollmentEntityMapper {

    /**
     * Converts a domain Enrollment to a JPA entity.
     *
     * @param enrollment the domain enrollment
     * @return the JPA entity
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToEnum")
    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "updatedAt", source = "auditInfo.updatedAt")
    @Mapping(target = "createdBy", source = "auditInfo.createdBy")
    @Mapping(target = "updatedBy", source = "auditInfo.updatedBy")
    EnrollmentJpaEntity toJpaEntity(Enrollment enrollment);

    /**
     * Converts a JPA entity to a domain Enrollment.
     *
     * @param entity the JPA entity
     * @return the domain enrollment
     */
    default Enrollment toDomain(EnrollmentJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Enrollment.fromRepository(
            entity.getId(),
            entity.getStudentId(),
            entity.getAcademicYear(),
            entity.getGradeClass(),
            entity.getSection(),
            entity.getEnrollmentDate(),
            entity.getWithdrawalDate(),
            enumToStatus(entity.getStatus()),
            entity.getRemarks(),
            toAuditInfo(entity),
            entity.getVersion()
        );
    }

    /**
     * Converts domain EnrollmentStatus to JPA enum.
     */
    @Named("statusToEnum")
    default EnrollmentJpaEntity.EnrollmentStatusEnum statusToEnum(EnrollmentStatus status) {
        return status != null ? EnrollmentJpaEntity.EnrollmentStatusEnum.valueOf(status.name()) : null;
    }

    /**
     * Converts JPA enum to domain EnrollmentStatus.
     */
    @Named("enumToStatus")
    default EnrollmentStatus enumToStatus(EnrollmentJpaEntity.EnrollmentStatusEnum status) {
        return status != null ? EnrollmentStatus.valueOf(status.name()) : null;
    }

    /**
     * Extracts AuditInfo from JPA entity.
     */
    @Named("toAuditInfo")
    default AuditInfo toAuditInfo(EnrollmentJpaEntity entity) {
        return new AuditInfo(
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getCreatedBy(),
            entity.getUpdatedBy()
        );
    }
}
