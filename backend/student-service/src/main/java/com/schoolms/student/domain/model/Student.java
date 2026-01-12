package com.schoolms.student.domain.model;

import com.schoolms.student.domain.exception.InvalidAgeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;

/**
 * Student Domain Model (BE-007)
 * Rich domain model with business logic
 */
public class Student {

    // Value Objects and Fields
    private StudentId id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String adhaarNumber;
    private String address;
    private String identificationMarks;
    private String guardianName;
    private String motherName;
    private String phone;
    private String email;
    private StudentStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer version;

    // Private constructor (use factory methods)
    private Student() {
    }

    /**
     * Factory Method for student registration (Domain-driven creation)
     */
    public static Student register(
        String studentId,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String adhaarNumber,
        String phone,
        String email,
        String address,
        String guardianName,
        String motherName,
        String identificationMarks
    ) {
        Student student = new Student();
        student.id = new StudentId(studentId);
        student.firstName = firstName;
        student.lastName = lastName;
        student.dateOfBirth = dateOfBirth;
        student.adhaarNumber = adhaarNumber;
        student.phone = phone;
        student.email = email;
        student.address = address;
        student.guardianName = guardianName;
        student.motherName = motherName;
        student.identificationMarks = identificationMarks;
        student.status = StudentStatus.ACTIVE;
        student.createdAt = Instant.now();
        student.updatedAt = Instant.now();
        student.version = 0;

        student.validate();

        return student;
    }

    /**
     * Factory method for reconstruction from persistence
     */
    public static Student reconstruct(
        StudentId id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String adhaarNumber,
        String phone,
        String email,
        String address,
        String guardianName,
        String motherName,
        String identificationMarks,
        StudentStatus status,
        Instant createdAt,
        Instant updatedAt,
        Integer version
    ) {
        Student student = new Student();
        student.id = id;
        student.firstName = firstName;
        student.lastName = lastName;
        student.dateOfBirth = dateOfBirth;
        student.adhaarNumber = adhaarNumber;
        student.phone = phone;
        student.email = email;
        student.address = address;
        student.guardianName = guardianName;
        student.motherName = motherName;
        student.identificationMarks = identificationMarks;
        student.status = status;
        student.createdAt = createdAt;
        student.updatedAt = updatedAt;
        student.version = version;
        return student;
    }

    /**
     * Domain Behavior: Calculate age from date of birth
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Domain Behavior: Update allowed fields only
     */
    public void updateProfile(String firstName, String lastName, String phone) {
        if (firstName != null && !firstName.isBlank()) {
            this.firstName = firstName;
        }
        if (lastName != null && !lastName.isBlank()) {
            this.lastName = lastName;
        }
        if (phone != null && !phone.isBlank()) {
            this.phone = phone;
        }
        this.updatedAt = Instant.now();
        validate();
    }

    /**
     * Domain Behavior: Activate student
     */
    public void activate() {
        this.status = StudentStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    /**
     * Domain Behavior: Deactivate student
     */
    public void deactivate() {
        this.status = StudentStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    /**
     * Domain Validation
     */
    private void validate() {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth is required");
        }

        int age = getAge();
        if (age < 3 || age > 18) {
            throw new InvalidAgeException(
                String.format("Age %d is outside allowed range (3-18 years)", age)
            );
        }

        if (adhaarNumber == null || !adhaarNumber.matches("^\\d{12}$")) {
            throw new IllegalArgumentException("Adhaar number must be exactly 12 digits");
        }

        if (phone == null || !phone.matches("^\\d{10}$")) {
            throw new IllegalArgumentException("Phone must be exactly 10 digits");
        }

        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (guardianName == null || guardianName.isBlank()) {
            throw new IllegalArgumentException("Guardian name is required");
        }

        if (motherName == null || motherName.isBlank()) {
            throw new IllegalArgumentException("Mother name is required");
        }

        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address is required");
        }
    }

    // Getters only (no setters - immutability enforced)
    public StudentId getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAdhaarNumber() {
        return adhaarNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getIdentificationMarks() {
        return identificationMarks;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public String getMotherName() {
        return motherName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Integer getVersion() {
        return version;
    }

    // Package-private setter for ID (used by infrastructure layer)
    void setId(StudentId id) {
        this.id = id;
    }

    // Package-private setter for version (used by infrastructure layer)
    void setVersion(Integer version) {
        this.version = version;
    }
}
