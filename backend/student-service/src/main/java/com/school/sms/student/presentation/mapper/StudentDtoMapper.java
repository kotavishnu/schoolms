package com.school.sms.student.presentation.mapper;

import com.school.sms.student.domain.model.*;
import com.school.sms.student.presentation.dto.request.CreateStudentRequest;
import com.school.sms.student.presentation.dto.response.PagedStudentResponse;
import com.school.sms.student.presentation.dto.response.StudentResponse;
import com.school.sms.student.presentation.dto.response.StudentSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between Student domain entity and DTOs.
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Mapper(componentModel = "spring")
public interface StudentDtoMapper {

    /**
     * Converts CreateStudentRequest to domain Student (for creation).
     *
     * @param request the create student request
     * @param studentId the generated student ID
     * @param createdBy the user creating the student
     * @return the domain student
     */
    default Student toDomain(CreateStudentRequest request, StudentId studentId, String createdBy) {
        PersonalInfo personalInfo = new PersonalInfo(
            request.getFirstName(),
            request.getLastName(),
            request.getDateOfBirth(),
            request.getIdentificationMark(),
            request.getAadhaarNumber()
        );

        ContactInfo contactInfo = new ContactInfo(
            request.getMobile(),
            request.getEmail(),
            request.getAddress()
        );

        FamilyInfo familyInfo = new FamilyInfo(
            request.getFathersName(),
            request.getMothersName()
        );

        return Student.createNew(studentId, personalInfo, contactInfo, familyInfo, createdBy);
    }

    /**
     * Converts domain Student to StudentResponse DTO.
     *
     * @param student the domain student
     * @return the response DTO
     */
    @Mapping(target = "studentId", source = "studentId.value")
    @Mapping(target = "firstName", source = "personalInfo.firstName")
    @Mapping(target = "lastName", source = "personalInfo.lastName")
    @Mapping(target = "dateOfBirth", source = "personalInfo.dateOfBirth")
    @Mapping(target = "age", expression = "java(student.calculateAge())")
    @Mapping(target = "identificationMark", source = "personalInfo.identificationMark")
    @Mapping(target = "aadhaarNumber", source = "personalInfo.aadhaarNumber")
    @Mapping(target = "mobile", source = "contactInfo.mobile")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "address", source = "contactInfo.address")
    @Mapping(target = "fathersName", source = "familyInfo.fathersName")
    @Mapping(target = "mothersName", source = "familyInfo.mothersName")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "updatedAt", source = "auditInfo.updatedAt")
    @Mapping(target = "createdBy", source = "auditInfo.createdBy")
    @Mapping(target = "updatedBy", source = "auditInfo.updatedBy")
    StudentResponse toResponse(Student student);

    /**
     * Converts domain Student to StudentSummaryResponse DTO.
     *
     * @param student the domain student
     * @return the summary response DTO
     */
    @Mapping(target = "studentId", source = "studentId.value")
    @Mapping(target = "firstName", source = "personalInfo.firstName")
    @Mapping(target = "lastName", source = "personalInfo.lastName")
    @Mapping(target = "mobile", source = "contactInfo.mobile")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    StudentSummaryResponse toSummaryResponse(Student student);

    /**
     * Converts a Page of Students to PagedStudentResponse.
     *
     * @param page the page of students
     * @return the paged response
     */
    default PagedStudentResponse toPagedResponse(Page<Student> page) {
        List<StudentSummaryResponse> content = page.getContent()
            .stream()
            .map(this::toSummaryResponse)
            .collect(Collectors.toList());

        PagedStudentResponse.PageableInfo pageInfo = new PagedStudentResponse.PageableInfo(
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );

        return new PagedStudentResponse(content, pageInfo);
    }

    /**
     * Converts StudentStatus enum to String.
     */
    @Named("statusToString")
    default String statusToString(StudentStatus status) {
        return status != null ? status.name() : null;
    }
}
