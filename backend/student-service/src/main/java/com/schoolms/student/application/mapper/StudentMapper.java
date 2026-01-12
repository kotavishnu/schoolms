package com.schoolms.student.application.mapper;

import com.schoolms.student.domain.model.Student;
import com.schoolms.student.presentation.dto.response.StudentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import java.util.List;

/**
 * Student Mapper (BE-020)
 * Maps between domain models and DTOs using MapStruct
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentMapper {

    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "age", target = "age")
    StudentResponse toResponse(Student student);

    List<StudentResponse> toResponseList(List<Student> students);
}
