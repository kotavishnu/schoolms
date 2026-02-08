package com.school.student.application.mapper;

import com.school.student.domain.model.Student;
import com.school.student.domain.model.StudentStatus;
import com.school.student.domain.model.valueobject.GuardianInfo;
import com.school.student.domain.model.valueobject.Mobile;
import com.school.student.presentation.dto.StudentRequestDTO;
import com.school.student.presentation.dto.StudentResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for StudentDTOMapper.
 * Tests DTO-domain mappings with null handling.
 *
 * <p>Following D-013: Infrastructure Layer Testing.
 */
@DisplayName("StudentDTOMapper Unit Tests")
class StudentDTOMapperTest {

    private StudentDTOMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(StudentDTOMapper.class);
    }

    // ==================== DTO to Domain Tests ====================

    @Test
    @DisplayName("Should convert StudentRequestDTO to domain Student with all fields")
    void shouldConvertRequestDTOToDomainWithAllFields() {
        // Arrange
        StudentRequestDTO dto = StudentRequestDTO.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .email("john.doe@example.com")
            .address("123 Main St")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .identificationMark("Mole on left arm")
            .aadhaarNumber("123456789012")
            .build();

        // Act
        Student student = mapper.toDomain(dto);

        // Assert
        assertThat(student).isNotNull();
        assertThat(student.getFirstName()).isEqualTo("John");
        assertThat(student.getLastName()).isEqualTo("Doe");
        assertThat(student.getDateOfBirth()).isEqualTo(LocalDate.of(2015, 5, 15));
        assertThat(student.getMobile()).isNotNull();
        assertThat(student.getMobile().getNumber()).isEqualTo("9876543210");
        assertThat(student.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(student.getAddress()).isEqualTo("123 Main St");
        assertThat(student.getGuardianInfo()).isNotNull();
        assertThat(student.getGuardianInfo().getFathersName()).isEqualTo("James Doe");
        assertThat(student.getGuardianInfo().getMothersName()).isEqualTo("Jane Doe");
        assertThat(student.getIdentificationMark()).isEqualTo("Mole on left arm");
        assertThat(student.getAadhaarNumber()).isEqualTo("123456789012");
        assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should convert StudentRequestDTO with only required fields")
    void shouldConvertRequestDTOWithOnlyRequiredFields() {
        // Arrange
        StudentRequestDTO dto = StudentRequestDTO.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .build();

        // Act
        Student student = mapper.toDomain(dto);

        // Assert
        assertThat(student).isNotNull();
        assertThat(student.getFirstName()).isEqualTo("John");
        assertThat(student.getLastName()).isEqualTo("Doe");
        assertThat(student.getMobile().getNumber()).isEqualTo("9876543210");
        assertThat(student.getGuardianInfo()).isNotNull();
        assertThat(student.getEmail()).isNull();
        assertThat(student.getAddress()).isNull();
        assertThat(student.getIdentificationMark()).isNull();
        assertThat(student.getAadhaarNumber()).isNull();
    }

    @Test
    @DisplayName("Should handle null DTO in toDomain")
    void shouldHandleNullDTOInToDomain() {
        // Act
        Student student = mapper.toDomain(null);

        // Assert
        assertThat(student).isNull();
    }

    @Test
    @DisplayName("Should set optional fields when provided")
    void shouldSetOptionalFieldsWhenProvided() {
        // Arrange
        StudentRequestDTO dto = StudentRequestDTO.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .email("john.doe@example.com")
            .address("123 Main St")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .identificationMark("Mole on left arm")
            .aadhaarNumber("123456789012")
            .build();

        // Act
        Student student = mapper.toDomain(dto);

        // Assert
        assertThat(student.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(student.getAddress()).isEqualTo("123 Main St");
        assertThat(student.getIdentificationMark()).isEqualTo("Mole on left arm");
        assertThat(student.getAadhaarNumber()).isEqualTo("123456789012");
    }

    // ==================== Domain to Response DTO Tests ====================

    @Test
    @DisplayName("Should convert domain Student to StudentResponseDTO")
    void shouldConvertDomainToResponseDTO() {
        // Arrange
        Student student = Student.register(
            "John",
            "Doe",
            LocalDate.of(2015, 5, 15),
            Mobile.of("9876543210"),
            GuardianInfo.of("James Doe", "Jane Doe")
        );
        student.setId(1L);
        student.setStudentId("STD-20260204-0001");
        student.setEmail("john.doe@example.com");
        student.setAddress("123 Main St");
        student.setIdentificationMark("Mole on left arm");
        student.setAadhaarNumber("123456789012");
        student.setVersion(0L);
        student.setCreatedAt(LocalDateTime.of(2026, 2, 4, 10, 30));
        student.setUpdatedAt(LocalDateTime.of(2026, 2, 4, 10, 30));
        student.setCreatedBy("admin");
        student.setUpdatedBy("admin");

        // Act
        StudentResponseDTO dto = mapper.toResponseDTO(student);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.studentId()).isEqualTo("STD-20260204-0001");
        assertThat(dto.firstName()).isEqualTo("John");
        assertThat(dto.lastName()).isEqualTo("Doe");
        assertThat(dto.dateOfBirth()).isEqualTo(LocalDate.of(2015, 5, 15));
        assertThat(dto.mobile()).isEqualTo("9876543210");
        assertThat(dto.email()).isEqualTo("john.doe@example.com");
        assertThat(dto.address()).isEqualTo("123 Main St");
        assertThat(dto.fathersName()).isEqualTo("James Doe");
        assertThat(dto.mothersName()).isEqualTo("Jane Doe");
        assertThat(dto.identificationMark()).isEqualTo("Mole on left arm");
        assertThat(dto.aadhaarNumber()).isEqualTo("123456789012");
        assertThat(dto.status()).isEqualTo("ACTIVE");
        assertThat(dto.version()).isEqualTo(0L);
        assertThat(dto.createdAt()).isEqualTo(LocalDateTime.of(2026, 2, 4, 10, 30));
        assertThat(dto.updatedAt()).isEqualTo(LocalDateTime.of(2026, 2, 4, 10, 30));
        assertThat(dto.createdBy()).isEqualTo("admin");
        assertThat(dto.updatedBy()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Should handle null student in toResponseDTO")
    void shouldHandleNullStudentInToResponseDTO() {
        // Act
        StudentResponseDTO dto = mapper.toResponseDTO(null);

        // Assert
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Should handle INACTIVE status in toResponseDTO")
    void shouldHandleInactiveStatusInToResponseDTO() {
        // Arrange
        Student student = Student.register(
            "John",
            "Doe",
            LocalDate.of(2015, 5, 15),
            Mobile.of("9876543210"),
            GuardianInfo.of("James Doe", "Jane Doe")
        );
        student.setId(1L);
        student.setStudentId("STD-20260204-0001");
        student.deactivate();

        // Act
        StudentResponseDTO dto = mapper.toResponseDTO(student);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.status()).isEqualTo("INACTIVE");
    }


    // ==================== Bidirectional Conversion Tests ====================

    @Test
    @DisplayName("Should maintain data integrity in bidirectional conversion")
    void shouldMaintainDataIntegrityInBidirectionalConversion() {
        // Arrange
        StudentRequestDTO requestDTO = StudentRequestDTO.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .email("john.doe@example.com")
            .address("123 Main St")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .identificationMark("Mole on left arm")
            .aadhaarNumber("123456789012")
            .build();

        // Act
        Student student = mapper.toDomain(requestDTO);
        student.setId(1L);
        student.setStudentId("STD-20260204-0001");
        StudentResponseDTO responseDTO = mapper.toResponseDTO(student);

        // Assert
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.firstName()).isEqualTo(requestDTO.firstName());
        assertThat(responseDTO.lastName()).isEqualTo(requestDTO.lastName());
        assertThat(responseDTO.dateOfBirth()).isEqualTo(requestDTO.dateOfBirth());
        assertThat(responseDTO.mobile()).isEqualTo(requestDTO.mobile());
        assertThat(responseDTO.email()).isEqualTo(requestDTO.email());
        assertThat(responseDTO.address()).isEqualTo(requestDTO.address());
        assertThat(responseDTO.fathersName()).isEqualTo(requestDTO.fathersName());
        assertThat(responseDTO.mothersName()).isEqualTo(requestDTO.mothersName());
        assertThat(responseDTO.identificationMark()).isEqualTo(requestDTO.identificationMark());
        assertThat(responseDTO.aadhaarNumber()).isEqualTo(requestDTO.aadhaarNumber());
    }

    @Test
    @DisplayName("Should handle missing optional fields in bidirectional conversion")
    void shouldHandleMissingOptionalFieldsInBidirectionalConversion() {
        // Arrange
        StudentRequestDTO requestDTO = StudentRequestDTO.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            // No optional fields set
            .build();

        // Act
        Student student = mapper.toDomain(requestDTO);
        student.setId(1L);
        student.setStudentId("STD-20260204-0001");
        StudentResponseDTO responseDTO = mapper.toResponseDTO(student);

        // Assert
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.firstName()).isEqualTo("John");
        assertThat(responseDTO.lastName()).isEqualTo("Doe");
        assertThat(responseDTO.mobile()).isEqualTo("9876543210");
        assertThat(responseDTO.email()).isNull();
        assertThat(responseDTO.address()).isNull();
        assertThat(responseDTO.identificationMark()).isNull();
        assertThat(responseDTO.aadhaarNumber()).isNull();
    }
}
