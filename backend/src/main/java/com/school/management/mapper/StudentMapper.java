package com.school.management.mapper;

import com.school.management.dto.request.StudentRequestDTO;
import com.school.management.dto.response.StudentResponseDTO;
import com.school.management.model.Student;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Student entity and DTOs.
 *
 * Automatically generates implementation for entity-DTO conversions.
 *
 * @author School Management Team
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    /**
     * Convert Student entity to response DTO
     * @param student Student entity
     * @return StudentResponseDTO
     */
    @Mapping(target = "fullName", expression = "java(student.getFullName())")
    @Mapping(target = "age", expression = "java(student.getAge())")
    @Mapping(target = "rollNumber", expression = "java(student.getRollNumber())")
    @Mapping(target = "classId", source = "schoolClass.id")
    @Mapping(target = "className", expression = "java(student.getSchoolClass() != null ? student.getSchoolClass().getDisplayName() : null)")
    @Mapping(target = "classNumber", source = "schoolClass.classNumber")
    @Mapping(target = "section", source = "schoolClass.section")
    StudentResponseDTO toResponseDTO(Student student);

    /**
     * Convert list of Student entities to list of response DTOs
     * @param students List of students
     * @return List of StudentResponseDTOs
     */
    List<StudentResponseDTO> toResponseDTOList(List<Student> students);

    /**
     * Convert request DTO to Student entity
     * Note: SchoolClass must be set separately by service layer
     * @param dto StudentRequestDTO
     * @return Student entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schoolClass", ignore = true)
    @Mapping(target = "enrollmentDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "feeJournals", ignore = true)
    @Mapping(target = "feeReceipts", ignore = true)
    Student toEntity(StudentRequestDTO dto);

    /**
     * Update existing entity from request DTO
     * @param dto StudentRequestDTO
     * @param student Existing student entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schoolClass", ignore = true)
    @Mapping(target = "enrollmentDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "feeJournals", ignore = true)
    @Mapping(target = "feeReceipts", ignore = true)
    void updateEntityFromDTO(StudentRequestDTO dto, @MappingTarget Student student);
}
