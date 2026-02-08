package com.school.student.infrastructure.drools;

import com.school.student.domain.model.valueobject.GuardianInfo;
import com.school.student.domain.model.valueobject.Mobile;
import com.school.student.domain.repository.StudentRepository;
import java.time.LocalDate;
import java.time.Period;

/**
 * Helper class for Drools rules to perform various validations.
 * Provides methods that can be called from DRL files without using eval().
 */
public class StudentValidationHelper {

    // ===== Age Validations =====

    public static int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return -1;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public static boolean isInFuture(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isAfter(LocalDate.now());
    }

    public static boolean isValidAge(LocalDate dateOfBirth) {
        int age = calculateAge(dateOfBirth);
        return age >= 3 && age <= 18;
    }

    // ===== Mobile Number Validations =====

    public static boolean isValidMobileFormat(Mobile mobile) {
        if (mobile == null || mobile.getNumber() == null) {
            return false;
        }
        return mobile.getNumber().matches("^\\d{10}$");
    }

    public static boolean mobileExistsForNewStudent(Mobile mobile, StudentRepository repository) {
        if (mobile == null || repository == null) {
            return false;
        }
        return repository.existsByMobile(mobile);
    }

    public static boolean mobileExistsForOtherStudent(Mobile mobile, Long studentId, StudentRepository repository) {
        if (mobile == null || studentId == null || repository == null) {
            return false;
        }
        return repository.existsByMobileAndIdNot(mobile, studentId);
    }

    // ===== Name Validations =====

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return name.matches("^[a-zA-Z\\s]+$");
    }

    // ===== Guardian Validations =====

    public static boolean hasAtLeastOneGuardianName(GuardianInfo guardianInfo) {
        if (guardianInfo == null) {
            return false;
        }
        return guardianInfo.getFathersName() != null || guardianInfo.getMothersName() != null;
    }
}
