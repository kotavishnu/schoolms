package com.school.sms.student.application.mapper;

import com.school.sms.student.application.dto.AddressDTO;
import com.school.sms.student.application.dto.StudentDTO;
import com.school.sms.student.domain.entity.Student;
import com.school.sms.student.domain.valueobject.Address;
import com.school.sms.student.presentation.dto.CreateStudentRequest;
import com.school.sms.student.presentation.dto.UpdateStudentRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-18T09:16:18+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class StudentMapperImpl implements StudentMapper {

    @Override
    public Student toEntity(CreateStudentRequest request) {
        if ( request == null ) {
            return null;
        }

        Student.StudentBuilder student = Student.builder();

        student.address( createStudentRequestToAddress( request ) );
        student.firstName( request.getFirstName() );
        student.lastName( request.getLastName() );
        student.dateOfBirth( request.getDateOfBirth() );
        student.mobile( request.getMobile() );
        student.email( request.getEmail() );
        student.fatherNameOrGuardian( request.getFatherNameOrGuardian() );
        student.motherName( request.getMotherName() );
        student.caste( request.getCaste() );
        student.moles( request.getMoles() );
        student.aadhaarNumber( request.getAadhaarNumber() );

        return student.build();
    }

    @Override
    public StudentDTO toDTO(Student student) {
        if ( student == null ) {
            return null;
        }

        StudentDTO.StudentDTOBuilder studentDTO = StudentDTO.builder();

        studentDTO.studentId( student.getStudentId() );
        studentDTO.firstName( student.getFirstName() );
        studentDTO.lastName( student.getLastName() );
        studentDTO.dateOfBirth( student.getDateOfBirth() );
        studentDTO.address( toAddressDTO( student.getAddress() ) );
        studentDTO.mobile( student.getMobile() );
        studentDTO.email( student.getEmail() );
        studentDTO.fatherNameOrGuardian( student.getFatherNameOrGuardian() );
        studentDTO.motherName( student.getMotherName() );
        studentDTO.caste( student.getCaste() );
        studentDTO.moles( student.getMoles() );
        studentDTO.aadhaarNumber( student.getAadhaarNumber() );
        studentDTO.status( student.getStatus() );
        studentDTO.createdAt( student.getCreatedAt() );
        studentDTO.updatedAt( student.getUpdatedAt() );
        studentDTO.createdBy( student.getCreatedBy() );
        studentDTO.updatedBy( student.getUpdatedBy() );
        studentDTO.version( student.getVersion() );

        studentDTO.age( student.calculateAge() );

        return studentDTO.build();
    }

    @Override
    public AddressDTO toAddressDTO(Address address) {
        if ( address == null ) {
            return null;
        }

        AddressDTO.AddressDTOBuilder addressDTO = AddressDTO.builder();

        addressDTO.street( address.getStreet() );
        addressDTO.city( address.getCity() );
        addressDTO.state( address.getState() );
        addressDTO.pinCode( address.getPinCode() );
        addressDTO.country( address.getCountry() );

        return addressDTO.build();
    }

    @Override
    public void updateEntityFromRequest(UpdateStudentRequest request, Student student) {
        if ( request == null ) {
            return;
        }

        if ( student.getAddress() == null ) {
            student.setAddress( Address.builder().build() );
        }
        updateStudentRequestToAddress( request, student.getAddress() );
        student.setFirstName( request.getFirstName() );
        student.setLastName( request.getLastName() );
        student.setDateOfBirth( request.getDateOfBirth() );
        student.setMobile( request.getMobile() );
        student.setEmail( request.getEmail() );
        student.setFatherNameOrGuardian( request.getFatherNameOrGuardian() );
        student.setMotherName( request.getMotherName() );
        student.setCaste( request.getCaste() );
        student.setMoles( request.getMoles() );
        student.setAadhaarNumber( request.getAadhaarNumber() );
        student.setVersion( request.getVersion() );
    }

    protected Address createStudentRequestToAddress(CreateStudentRequest createStudentRequest) {
        if ( createStudentRequest == null ) {
            return null;
        }

        Address.AddressBuilder address = Address.builder();

        address.street( createStudentRequest.getStreet() );
        address.city( createStudentRequest.getCity() );
        address.state( createStudentRequest.getState() );
        address.pinCode( createStudentRequest.getPinCode() );

        address.country( "India" );

        return address.build();
    }

    protected void updateStudentRequestToAddress(UpdateStudentRequest updateStudentRequest, Address mappingTarget) {
        if ( updateStudentRequest == null ) {
            return;
        }

        mappingTarget.setStreet( updateStudentRequest.getStreet() );
        mappingTarget.setCity( updateStudentRequest.getCity() );
        mappingTarget.setState( updateStudentRequest.getState() );
        mappingTarget.setPinCode( updateStudentRequest.getPinCode() );

        mappingTarget.setCountry( "India" );
    }
}
