package com.school.sms.student.application.mapper;

import com.school.sms.student.application.dto.AddressDTO;
import com.school.sms.student.application.dto.StudentDTO;
import com.school.sms.student.domain.entity.Student;
import com.school.sms.student.domain.valueobject.Address;
import com.school.sms.student.presentation.dto.CreateStudentRequest;
import com.school.sms.student.presentation.dto.UpdateStudentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for Student entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface StudentMapper {

    /**
     * Maps CreateStudentRequest to Student entity.
     *
     * @param request the create request
     * @return Student entity
     */
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "address.street", source = "street")
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.state", source = "state")
    @Mapping(target = "address.pinCode", source = "pinCode")
    @Mapping(target = "address.country", constant = "India")
    Student toEntity(CreateStudentRequest request);

    /**
     * Maps Student entity to StudentDTO.
     *
     * @param student the Student entity
     * @return StudentDTO
     */
    @Mapping(target = "age", expression = "java(student.calculateAge())")
    StudentDTO toDTO(Student student);

    /**
     * Maps Address value object to AddressDTO.
     *
     * @param address the Address value object
     * @return AddressDTO
     */
    AddressDTO toAddressDTO(Address address);

    /**
     * Updates Student entity from UpdateStudentRequest.
     *
     * @param request the update request
     * @param student the target student entity
     */
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "address.street", source = "street")
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.state", source = "state")
    @Mapping(target = "address.pinCode", source = "pinCode")
    @Mapping(target = "address.country", constant = "India")
    void updateEntityFromRequest(UpdateStudentRequest request, @MappingTarget Student student);
}
