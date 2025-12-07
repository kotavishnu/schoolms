package com.school.sms.student.domain.model;

import com.school.sms.student.domain.exception.InvalidAgeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Student Domain Model Tests")
class StudentTest {

    @Test
    @DisplayName("Create student with valid age (10 years)")
    void createNew_WithValidAge_ShouldSucceed() {
        StudentId studentId = StudentId.of("STD-20241207-0001");
        PersonalInfo personalInfo = new PersonalInfo("John", "Doe", LocalDate.now().minusYears(10), "Mole", null);
        ContactInfo contactInfo = new ContactInfo("9876543210", "john@test.com", "123 Main St");
        FamilyInfo familyInfo = new FamilyInfo("Robert Doe", "Jane Doe");

        Student student = Student.createNew(studentId, personalInfo, contactInfo, familyInfo, "admin");

        assertThat(student).isNotNull();
        assertThat(student.getStudentId()).isEqualTo(studentId);
        assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Create student with minimum age (3 years)")
    void createNew_WithMinimumAge_ShouldSucceed() {
        StudentId studentId = StudentId.of("STD-20241207-0002");
        PersonalInfo personalInfo = new PersonalInfo("Jane", "Doe", LocalDate.now().minusYears(3), "Scar", null);
        ContactInfo contactInfo = new ContactInfo("9876543211", "jane@test.com", "456 Oak St");
        FamilyInfo familyInfo = new FamilyInfo("Robert Doe", "Jane Doe");

        Student student = Student.createNew(studentId, personalInfo, contactInfo, familyInfo, "admin");

        assertThat(student).isNotNull();
        assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Create student with maximum age (18 years)")
    void createNew_WithMaximumAge_ShouldSucceed() {
        StudentId studentId = StudentId.of("STD-20241207-0003");
        PersonalInfo personalInfo = new PersonalInfo("Jack", "Smith", LocalDate.now().minusYears(18), "Mark", null);
        ContactInfo contactInfo = new ContactInfo("9876543212", "jack@test.com", "789 Elm St");
        FamilyInfo familyInfo = new FamilyInfo("John Smith", "Mary Smith");

        Student student = Student.createNew(studentId, personalInfo, contactInfo, familyInfo, "admin");

        assertThat(student).isNotNull();
        assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Reject student with age below minimum (2 years)")
    void createNew_WithAgeTooYoung_ShouldThrowException() {
        StudentId studentId = StudentId.of("STD-20241207-0004");
        PersonalInfo personalInfo = new PersonalInfo("Baby", "Doe", LocalDate.now().minusYears(2), "None", null);
        ContactInfo contactInfo = new ContactInfo("9876543213", "baby@test.com", "101 Pine St");
        FamilyInfo familyInfo = new FamilyInfo("Robert Doe", "Jane Doe");

        assertThatThrownBy(() -> Student.createNew(studentId, personalInfo, contactInfo, familyInfo, "admin"))
            .isInstanceOf(InvalidAgeException.class);
    }

    @Test
    @DisplayName("Reject student with age above maximum (19 years)")
    void createNew_WithAgeTooOld_ShouldThrowException() {
        StudentId studentId = StudentId.of("STD-20241207-0005");
        PersonalInfo personalInfo = new PersonalInfo("Adult", "Doe", LocalDate.now().minusYears(19), "None", null);
        ContactInfo contactInfo = new ContactInfo("9876543214", "adult@test.com", "202 Cedar St");
        FamilyInfo familyInfo = new FamilyInfo("Robert Doe", "Jane Doe");

        assertThatThrownBy(() -> Student.createNew(studentId, personalInfo, contactInfo, familyInfo, "admin"))
            .isInstanceOf(InvalidAgeException.class);
    }

    @Test
    @DisplayName("Deactivate active student")
    void deactivate_ActiveStudent_ShouldChangeStatus() {
        Student student = createValidStudent();

        Student deactivated = student.deactivate("admin");

        assertThat(deactivated.getStatus()).isEqualTo(StudentStatus.INACTIVE);
    }

    @Test
    @DisplayName("FromRepository should recreate student")
    void fromRepository_WithValidData_ShouldRecreateStudent() {
        StudentId studentId = StudentId.of("STD-20241207-0001");
        PersonalInfo personalInfo = new PersonalInfo("John", "Doe", LocalDate.now().minusYears(10), "Mole", null);
        ContactInfo contactInfo = new ContactInfo("9876543210", "john@test.com", "123 Main St");
        FamilyInfo familyInfo = new FamilyInfo("Robert", "Jane");
        AuditInfo auditInfo = AuditInfo.forCreation("admin");

        Student student = Student.fromRepository(1L, studentId, personalInfo, contactInfo, familyInfo,
            StudentStatus.ACTIVE, auditInfo, 0L);

        assertThat(student).isNotNull();
        assertThat(student.getId()).isEqualTo(1L);
        assertThat(student.getStudentId()).isEqualTo(studentId);
    }

    private Student createValidStudent() {
        StudentId studentId = StudentId.of("STD-20241207-0001");
        PersonalInfo personalInfo = new PersonalInfo("John", "Doe", LocalDate.now().minusYears(10), "Mole", null);
        ContactInfo contactInfo = new ContactInfo("9876543210", "john@test.com", "123 Main St");
        FamilyInfo familyInfo = new FamilyInfo("Robert Doe", "Jane Doe");
        return Student.createNew(studentId, personalInfo, contactInfo, familyInfo, "admin");
    }
}
