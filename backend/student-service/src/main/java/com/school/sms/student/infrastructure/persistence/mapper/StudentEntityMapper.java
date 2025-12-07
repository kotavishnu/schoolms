package com.school.sms.student.infrastructure.persistence.mapper;

import com.school.sms.student.domain.model.*;
import com.school.sms.student.infrastructure.persistence.entity.StudentJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between Student domain entity and StudentJpaEntity.
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Mapper(componentModel = "spring")
public interface StudentEntityMapper {

    /**
     * Converts a domain Student to a JPA entity.
     *
     * @param student the domain student
     * @return the JPA entity
     */
    @Mapping(target = "studentId", source = "studentId", qualifiedByName = "studentIdToString")
    @Mapping(target = "firstName", source = "personalInfo.firstName")
    @Mapping(target = "lastName", source = "personalInfo.lastName")
    @Mapping(target = "dateOfBirth", source = "personalInfo.dateOfBirth")
    @Mapping(target = "identificationMark", source = "personalInfo.identificationMark")
    @Mapping(target = "aadhaarNumber", source = "personalInfo.aadhaarNumber")
    @Mapping(target = "mobile", source = "contactInfo.mobile")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "address", source = "contactInfo.address")
    @Mapping(target = "fathersName", source = "familyInfo.fathersName")
    @Mapping(target = "mothersName", source = "familyInfo.mothersName")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToEnum")
    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "updatedAt", source = "auditInfo.updatedAt")
    @Mapping(target = "createdBy", source = "auditInfo.createdBy")
    @Mapping(target = "updatedBy", source = "auditInfo.updatedBy")
    @Mapping(target = "version", source = "version")
    StudentJpaEntity toJpaEntity(Student student);

    /**
     * Converts a JPA entity to a domain Student.
     *
     * @param entity the JPA entity
     * @return the domain student
     */
    default Student toDomain(StudentJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Student.fromRepository(
            entity.getId(),
            StudentId.of(entity.getStudentId()),
            toPersonalInfo(entity),
            toContactInfo(entity),
            toFamilyInfo(entity),
            enumToStatus(entity.getStatus()),
            toAuditInfo(entity),
            entity.getVersion()
        );
    }

    /**
     * Converts StudentId value object to String.
     */
    @Named("studentIdToString")
    default String studentIdToString(StudentId studentId) {
        return studentId != null ? studentId.getValue() : null;
    }

    /**
     * Converts String to StudentId value object.
     */
    @Named("stringToStudentId")
    default StudentId stringToStudentId(String studentId) {
        return studentId != null ? StudentId.of(studentId) : null;
    }

    /**
     * Converts domain StudentStatus to JPA enum.
     */
    @Named("statusToEnum")
    default StudentJpaEntity.StudentStatusEnum statusToEnum(StudentStatus status) {
        return status != null ? StudentJpaEntity.StudentStatusEnum.valueOf(status.name()) : null;
    }

    /**
     * Converts JPA enum to domain StudentStatus.
     */
    @Named("enumToStatus")
    default StudentStatus enumToStatus(StudentJpaEntity.StudentStatusEnum status) {
        return status != null ? StudentStatus.valueOf(status.name()) : null;
    }

    /**
     * Extracts PersonalInfo from JPA entity.
     */
    @Named("toPersonalInfo")
    default PersonalInfo toPersonalInfo(StudentJpaEntity entity) {
        return new PersonalInfo(
            entity.getFirstName(),
            entity.getLastName(),
            entity.getDateOfBirth(),
            entity.getIdentificationMark(),
            entity.getAadhaarNumber()
        );
    }

    /**
     * Extracts ContactInfo from JPA entity.
     */
    @Named("toContactInfo")
    default ContactInfo toContactInfo(StudentJpaEntity entity) {
        return new ContactInfo(
            entity.getMobile(),
            entity.getEmail(),
            entity.getAddress()
        );
    }

    /**
     * Extracts FamilyInfo from JPA entity.
     */
    @Named("toFamilyInfo")
    default FamilyInfo toFamilyInfo(StudentJpaEntity entity) {
        return new FamilyInfo(
            entity.getFathersName(),
            entity.getMothersName()
        );
    }

    /**
     * Extracts AuditInfo from JPA entity.
     */
    @Named("toAuditInfo")
    default AuditInfo toAuditInfo(StudentJpaEntity entity) {
        return new AuditInfo(
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getCreatedBy(),
            entity.getUpdatedBy()
        );
    }
}
