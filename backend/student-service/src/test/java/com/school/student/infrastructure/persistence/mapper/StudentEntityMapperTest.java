package com.school.student.infrastructure.persistence.mapper;

import com.school.student.domain.model.Student;
import com.school.student.domain.model.StudentStatus;
import com.school.student.domain.model.valueobject.GuardianInfo;
import com.school.student.domain.model.valueobject.Mobile;
import com.school.student.infrastructure.persistence.entity.StudentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for StudentEntityMapper.
 * Tests MapStruct mappings between domain and entity.
 *
 * <p>Following D-013: Infrastructure Layer Testing - Test mappers for null handling and bidirectional conversions.
 */
@DisplayName("StudentEntityMapper Unit Tests")
class StudentEntityMapperTest {

    private StudentEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(StudentEntityMapper.class);
    }

    // ==================== Domain to Entity Tests ====================

    @Test
    @DisplayName("Should convert domain Student to StudentEntity")
    void shouldConvertDomainToEntity() {
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
        student.setCreatedBy("admin");

        // Act
        StudentEntity entity = mapper.toEntity(student);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getStudentId()).isEqualTo("STD-20260204-0001");
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.getDateOfBirth()).isEqualTo(LocalDate.of(2015, 5, 15));
        assertThat(entity.getMobile()).isEqualTo("9876543210");
        assertThat(entity.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(entity.getAddress()).isEqualTo("123 Main St");
        assertThat(entity.getFathersName()).isEqualTo("James Doe");
        assertThat(entity.getMothersName()).isEqualTo("Jane Doe");
        assertThat(entity.getIdentificationMark()).isEqualTo("Mole on left arm");
        assertThat(entity.getAadhaarNumber()).isEqualTo("123456789012");
        assertThat(entity.getStatus()).isEqualTo(StudentEntity.StudentStatusEnum.ACTIVE);
        assertThat(entity.getVersion()).isEqualTo(0L);
        assertThat(entity.getCreatedBy()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Should handle null student in toEntity")
    void shouldHandleNullStudentInToEntity() {
        // Act
        StudentEntity entity = mapper.toEntity(null);

        // Assert
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should handle INACTIVE status in toEntity")
    void shouldHandleInactiveStatusInToEntity() {
        // Arrange
        Student student = Student.register(
            "John",
            "Doe",
            LocalDate.of(2015, 5, 15),
            Mobile.of("9876543210"),
            GuardianInfo.of("James Doe", "Jane Doe")
        );
        student.deactivate();

        // Act
        StudentEntity entity = mapper.toEntity(student);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getStatus()).isEqualTo(StudentEntity.StudentStatusEnum.INACTIVE);
    }

    // ==================== Entity to Domain Tests ====================

    @Test
    @DisplayName("Should convert StudentEntity to domain Student")
    void shouldConvertEntityToDomain() {
        // Arrange
        StudentEntity entity = StudentEntity.builder()
            .id(1L)
            .studentId("STD-20260204-0001")
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
            .status(StudentEntity.StudentStatusEnum.ACTIVE)
            .version(0L)
            .createdBy("admin")
            .build();

        // Act
        Student student = mapper.toDomain(entity);

        // Assert
        assertThat(student).isNotNull();
        assertThat(student.getId()).isEqualTo(1L);
        assertThat(student.getStudentId()).isEqualTo("STD-20260204-0001");
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
        assertThat(student.getVersion()).isEqualTo(0L);
        assertThat(student.getCreatedBy()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Should handle null entity in toDomain")
    void shouldHandleNullEntityInToDomain() {
        // Act
        Student student = mapper.toDomain(null);

        // Assert
        assertThat(student).isNull();
    }

    @Test
    @DisplayName("Should handle INACTIVE status in toDomain")
    void shouldHandleInactiveStatusInToDomain() {
        // Arrange
        StudentEntity entity = StudentEntity.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .status(StudentEntity.StudentStatusEnum.INACTIVE)
            .build();

        // Act
        Student student = mapper.toDomain(entity);

        // Assert
        assertThat(student).isNotNull();
        assertThat(student.getStatus()).isEqualTo(StudentStatus.INACTIVE);
    }

    // ==================== Bidirectional Conversion Tests ====================

    @Test
    @DisplayName("Should maintain data integrity in bidirectional conversion")
    void shouldMaintainDataIntegrityInBidirectionalConversion() {
        // Arrange
        Student originalStudent = Student.register(
            "John",
            "Doe",
            LocalDate.of(2015, 5, 15),
            Mobile.of("9876543210"),
            GuardianInfo.of("James Doe", "Jane Doe")
        );
        originalStudent.setId(1L);
        originalStudent.setStudentId("STD-20260204-0001");
        originalStudent.setEmail("john.doe@example.com");
        originalStudent.setVersion(0L);

        // Act
        StudentEntity entity = mapper.toEntity(originalStudent);
        Student reconvertedStudent = mapper.toDomain(entity);

        // Assert
        assertThat(reconvertedStudent).isNotNull();
        assertThat(reconvertedStudent.getId()).isEqualTo(originalStudent.getId());
        assertThat(reconvertedStudent.getStudentId()).isEqualTo(originalStudent.getStudentId());
        assertThat(reconvertedStudent.getFirstName()).isEqualTo(originalStudent.getFirstName());
        assertThat(reconvertedStudent.getLastName()).isEqualTo(originalStudent.getLastName());
        assertThat(reconvertedStudent.getMobile().getNumber()).isEqualTo(originalStudent.getMobile().getNumber());
        assertThat(reconvertedStudent.getEmail()).isEqualTo(originalStudent.getEmail());
        assertThat(reconvertedStudent.getStatus()).isEqualTo(originalStudent.getStatus());
    }

    // ==================== Helper Method Tests ====================

    @Test
    @DisplayName("Should convert mobile to string")
    void shouldConvertMobileToString() {
        // Arrange
        Mobile mobile = Mobile.of("9876543210");

        // Act
        String result = mapper.mobileToString(mobile);

        // Assert
        assertThat(result).isEqualTo("9876543210");
    }

    @Test
    @DisplayName("Should handle null mobile in mobileToString")
    void shouldHandleNullMobileInMobileToString() {
        // Act
        String result = mapper.mobileToString(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should convert string to mobile")
    void shouldConvertStringToMobile() {
        // Act
        Mobile mobile = mapper.stringToMobile("9876543210");

        // Assert
        assertThat(mobile).isNotNull();
        assertThat(mobile.getNumber()).isEqualTo("9876543210");
    }

    @Test
    @DisplayName("Should handle null string in stringToMobile")
    void shouldHandleNullStringInStringToMobile() {
        // Act
        Mobile mobile = mapper.stringToMobile(null);

        // Assert
        assertThat(mobile).isNull();
    }

    @Test
    @DisplayName("Should extract fathers name from guardian info")
    void shouldExtractFathersNameFromGuardianInfo() {
        // Arrange
        GuardianInfo guardianInfo = GuardianInfo.of("James Doe", "Jane Doe");

        // Act
        String result = mapper.extractFathersName(guardianInfo);

        // Assert
        assertThat(result).isEqualTo("James Doe");
    }

    @Test
    @DisplayName("Should extract mothers name from guardian info")
    void shouldExtractMothersNameFromGuardianInfo() {
        // Arrange
        GuardianInfo guardianInfo = GuardianInfo.of("James Doe", "Jane Doe");

        // Act
        String result = mapper.extractMothersName(guardianInfo);

        // Assert
        assertThat(result).isEqualTo("Jane Doe");
    }

    @Test
    @DisplayName("Should convert domain status ACTIVE to entity status")
    void shouldConvertDomainStatusActiveToEntity() {
        // Act
        StudentEntity.StudentStatusEnum result = mapper.domainStatusToEntity(StudentStatus.ACTIVE);

        // Assert
        assertThat(result).isEqualTo(StudentEntity.StudentStatusEnum.ACTIVE);
    }

    @Test
    @DisplayName("Should convert domain status INACTIVE to entity status")
    void shouldConvertDomainStatusInactiveToEntity() {
        // Act
        StudentEntity.StudentStatusEnum result = mapper.domainStatusToEntity(StudentStatus.INACTIVE);

        // Assert
        assertThat(result).isEqualTo(StudentEntity.StudentStatusEnum.INACTIVE);
    }

    @Test
    @DisplayName("Should handle null status in domainStatusToEntity")
    void shouldHandleNullStatusInDomainStatusToEntity() {
        // Act
        StudentEntity.StudentStatusEnum result = mapper.domainStatusToEntity(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should convert entity status to domain status")
    void shouldConvertEntityStatusToDomain() {
        // Act
        StudentStatus result = mapper.entityStatusToDomain(StudentEntity.StudentStatusEnum.ACTIVE);

        // Assert
        assertThat(result).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should convert LocalDateTime to OffsetDateTime")
    void shouldConvertLocalDateTimeToOffsetDateTime() {
        // Arrange
        LocalDateTime localDateTime = LocalDateTime.of(2026, 2, 4, 10, 30, 0);

        // Act
        OffsetDateTime result = mapper.localDateTimeToOffsetDateTime(localDateTime);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.toLocalDateTime()).isEqualTo(localDateTime);
    }

    @Test
    @DisplayName("Should handle null LocalDateTime")
    void shouldHandleNullLocalDateTime() {
        // Act
        OffsetDateTime result = mapper.localDateTimeToOffsetDateTime(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should convert OffsetDateTime to LocalDateTime")
    void shouldConvertOffsetDateTimeToLocalDateTime() {
        // Arrange
        OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneId.systemDefault());

        // Act
        LocalDateTime result = mapper.offsetDateTimeToLocalDateTime(offsetDateTime);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(offsetDateTime.toLocalDateTime());
    }

    @Test
    @DisplayName("Should handle null OffsetDateTime")
    void shouldHandleNullOffsetDateTime() {
        // Act
        LocalDateTime result = mapper.offsetDateTimeToLocalDateTime(null);

        // Assert
        assertThat(result).isNull();
    }
}
