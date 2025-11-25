package com.school.sms.student.application.mapper;

import com.school.sms.student.application.dto.StudentPageResponse;
import com.school.sms.student.application.dto.StudentResponse;
import com.school.sms.student.domain.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

/**
 * MapStruct mapper for Student entity to DTOs.
 */
@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(source = "studentId.value", target = "studentId")
    @Mapping(source = "mobile.number", target = "mobile")
    @Mapping(target = "currentAge", expression = "java(student.getCurrentAge())")
    @Mapping(source = "status", target = "status")
    StudentResponse toResponse(Student student);

    default StudentPageResponse toPageResponse(Page<Student> page) {
        StudentPageResponse response = new StudentPageResponse();
        response.setContent(page.map(this::toResponse).getContent());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLast(page.isLast());
        response.setFirst(page.isFirst());
        return response;
    }

    default String map(Enum<?> value) {
        return value != null ? value.name() : null;
    }
}
