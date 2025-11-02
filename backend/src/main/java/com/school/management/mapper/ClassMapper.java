package com.school.management.mapper;

import com.school.management.dto.request.ClassRequestDTO;
import com.school.management.dto.response.ClassResponseDTO;
import com.school.management.model.SchoolClass;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClassMapper {

    @Mapping(target = "availableSeats", expression = "java(schoolClass.getAvailableSeats())")
    @Mapping(target = "displayName", expression = "java(schoolClass.getDisplayName())")
    @Mapping(target = "hasAvailableSeats", expression = "java(schoolClass.hasAvailableSeats())")
    @Mapping(target = "isAlmostFull", expression = "java(schoolClass.isAlmostFull())")
    ClassResponseDTO toResponseDTO(SchoolClass schoolClass);

    List<ClassResponseDTO> toResponseDTOList(List<SchoolClass> schoolClasses);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentEnrollment", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "students", ignore = true)
    SchoolClass toEntity(ClassRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentEnrollment", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "students", ignore = true)
    void updateEntityFromDTO(ClassRequestDTO dto, @MappingTarget SchoolClass schoolClass);
}
