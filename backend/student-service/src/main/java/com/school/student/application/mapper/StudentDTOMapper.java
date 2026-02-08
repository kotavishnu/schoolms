package com.school.student.application.mapper;

import com.school.student.domain.model.Student;
import com.school.student.domain.model.StudentStatus;
import com.school.student.domain.model.valueobject.GuardianInfo;
import com.school.student.domain.model.valueobject.Mobile;
import com.school.student.presentation.dto.StudentRequestDTO;
import com.school.student.presentation.dto.StudentResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for converting between Student DTOs and domain models.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>StudentRequestDTO → Student domain model</li>
 *   <li>Student domain model → StudentResponseDTO</li>
 *   <li>Handle value object conversions (Mobile, GuardianInfo)</li>
 *   <li>Handle enum conversions (StudentStatus)</li>
 * </ul>
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface StudentDTOMapper {

    /**
     * Convert StudentRequestDTO to Student domain model.
     * Uses Student.register() factory method.
     *
     * @param dto the request DTO
     * @return Student domain model
     */
    default Student toDomain(StudentRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Mobile mobile = Mobile.of(dto.mobile());
        GuardianInfo guardianInfo = GuardianInfo.of(dto.fathersName(), dto.mothersName());

        Student student = Student.register(
            dto.firstName(),
            dto.lastName(),
            dto.dateOfBirth(),
            mobile,
            guardianInfo
        );

        // Set optional fields
        if (dto.email() != null) {
            student.setEmail(dto.email());
        }
        if (dto.address() != null) {
            student.setAddress(dto.address());
        }
        if (dto.identificationMark() != null) {
            student.setIdentificationMark(dto.identificationMark());
        }
        if (dto.aadhaarNumber() != null) {
            student.setAadhaarNumber(dto.aadhaarNumber());
        }

        return student;
    }

    /**
     * Convert Student domain model to StudentResponseDTO.
     *
     * @param student the domain model
     * @return StudentResponseDTO
     */
    default StudentResponseDTO toResponseDTO(Student student) {
        if (student == null) {
            return null;
        }

        return StudentResponseDTO.builder()
            .id(student.getId())
            .studentId(student.getStudentId())
            .status(student.getStatus() != null ? student.getStatus().name() : null)
            .version(student.getVersion())
            .createdAt(student.getCreatedAt())
            .updatedAt(student.getUpdatedAt())
            .createdBy(student.getCreatedBy())
            .updatedBy(student.getUpdatedBy())
            .firstName(student.getFirstName())
            .lastName(student.getLastName())
            .dateOfBirth(student.getDateOfBirth())
            .mobile(student.getMobile() != null ? student.getMobile().getNumber() : null)
            .email(student.getEmail())
            .address(student.getAddress())
            .fathersName(student.getGuardianInfo() != null ? student.getGuardianInfo().getFathersName() : null)
            .mothersName(student.getGuardianInfo() != null ? student.getGuardianInfo().getMothersName() : null)
            .identificationMark(student.getIdentificationMark())
            .aadhaarNumber(student.getAadhaarNumber())
            .build();
    }
}
