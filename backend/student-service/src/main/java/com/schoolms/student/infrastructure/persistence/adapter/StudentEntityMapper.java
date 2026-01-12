package com.schoolms.student.infrastructure.persistence.adapter;

import com.schoolms.student.domain.model.Student;
import com.schoolms.student.domain.model.StudentId;
import com.schoolms.student.infrastructure.persistence.entity.StudentJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Student Entity Mapper (BE-014)
 * Maps between domain model and JPA entity
 */
@Component
public class StudentEntityMapper {

    /**
     * Convert domain model to JPA entity
     */
    public StudentJpaEntity toJpaEntity(Student domain) {
        if (domain == null) {
            return null;
        }

        StudentJpaEntity entity = new StudentJpaEntity();
        entity.setStudentId(domain.getId().getValue());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setDateOfBirth(domain.getDateOfBirth());
        entity.setAge(domain.getAge());
        entity.setAdhaarNumber(domain.getAdhaarNumber());
        entity.setAddress(domain.getAddress());
        entity.setIdentificationMarks(domain.getIdentificationMarks());
        entity.setGuardianName(domain.getGuardianName());
        entity.setMotherName(domain.getMotherName());
        entity.setPhone(domain.getPhone());
        entity.setEmail(domain.getEmail());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setVersion(domain.getVersion());

        return entity;
    }

    /**
     * Convert JPA entity to domain model
     */
    public Student toDomain(StudentJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Student.reconstruct(
            new StudentId(entity.getStudentId()),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getDateOfBirth(),
            entity.getAdhaarNumber(),
            entity.getPhone(),
            entity.getEmail(),
            entity.getAddress(),
            entity.getGuardianName(),
            entity.getMotherName(),
            entity.getIdentificationMarks(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getVersion()
        );
    }
}
