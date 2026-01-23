package com.school.student.service;

import com.school.student.domain.validation.StudentValidationRequest;
import com.school.student.domain.validation.ValidationResult;
import com.school.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

/**
 * Drools Validation Service
 *
 * Executes business rules validation using Drools engine.
 * Combines Drools rule validation with database uniqueness checks.
 *
 * Business Rules:
 * - BR-STU-001: Age validation (Drools)
 * - BR-STU-002: Mobile uniqueness (Database + Drools format)
 * - BR-STU-003: Email format (Drools)
 * - BR-STU-004: Aadhaar format (Drools)
 * - BR-STU-005: Name pattern (Drools)
 * - BR-STU-006: Mobile format (Drools)
 * - BR-STU-007: Editable fields (enforced in controller/service)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DroolsValidationService {

    private final KieContainer kieContainer;
    private final StudentRepository studentRepository;

    /**
     * Validate student data for registration (create operation)
     *
     * @param request Validation request with student data
     * @return ValidationResult with any errors found
     */
    public ValidationResult validateStudent(StudentValidationRequest request) {
        log.debug("Validating student: {} {}", request.getFirstName(), request.getLastName());

        ValidationResult result = new ValidationResult();

        // 1. Execute Drools rules
        executeRules(request, result);

        // 2. BR-STU-002: Mobile uniqueness check (database)
        if (request.isCheckMobileUniqueness() && request.getMobile() != null) {
            boolean mobileExists;

            if (request.getStudentId() != null) {
                // Update operation: check if mobile exists for different student
                mobileExists = studentRepository.existsByMobileAndStudentIdNot(
                    request.getMobile(),
                    request.getStudentId()
                );
            } else {
                // Create operation: check if mobile exists at all
                mobileExists = studentRepository.existsByMobile(request.getMobile());
            }

            if (mobileExists) {
                result.addError(
                    "mobile",
                    "MOBILE_ALREADY_EXISTS",
                    "Mobile number is already registered to another student"
                );
            }
        }

        // 3. Email uniqueness check (database)
        if (request.isCheckEmailUniqueness() && request.getEmail() != null) {
            boolean emailExists;

            if (request.getStudentId() != null) {
                emailExists = studentRepository.existsByEmailAndStudentIdNot(
                    request.getEmail(),
                    request.getStudentId()
                );
            } else {
                emailExists = studentRepository.existsByEmail(request.getEmail());
            }

            if (emailExists) {
                result.addError(
                    "email",
                    "EMAIL_ALREADY_EXISTS",
                    "Email address is already registered to another student"
                );
            }
        }

        // 4. Aadhaar uniqueness check (if provided)
        if (request.getAadhaarNumber() != null && !request.getAadhaarNumber().isEmpty()) {
            boolean aadhaarExists = studentRepository.existsByAadhaarNumber(request.getAadhaarNumber());

            if (aadhaarExists) {
                result.addError(
                    "aadhaarNumber",
                    "AADHAAR_ALREADY_EXISTS",
                    "Aadhaar number is already registered to another student"
                );
            }
        }

        log.debug("Validation result: valid={}, errorCount={}", result.isValid(), result.getErrorCount());
        return result;
    }

    /**
     * Validate student data for update operation
     * Only validates editable fields
     *
     * @param request Validation request with student data
     * @return ValidationResult with any errors found
     */
    public ValidationResult validateUpdate(StudentValidationRequest request) {
        log.debug("Validating student update: {}", request.getStudentId());

        // For updates, we only validate:
        // 1. First name format
        // 2. Last name format
        // 3. Mobile format and uniqueness
        // 4. Status (validated in DTO)

        ValidationResult result = new ValidationResult();

        // Execute Drools rules (will validate names and mobile format)
        executeRules(request, result);

        // Check mobile uniqueness
        if (request.getMobile() != null) {
            boolean mobileExists = studentRepository.existsByMobileAndStudentIdNot(
                request.getMobile(),
                request.getStudentId()
            );

            if (mobileExists) {
                result.addError(
                    "mobile",
                    "MOBILE_ALREADY_EXISTS",
                    "Mobile number is already registered to another student"
                );
            }
        }

        return result;
    }

    /**
     * Execute Drools rules
     *
     * @param request Validation request
     * @param result Result object to collect errors
     */
    private void executeRules(StudentValidationRequest request, ValidationResult result) {
        KieSession kieSession = kieContainer.newKieSession();
        try {
            // Set global variable for result collection
            kieSession.setGlobal("validationResult", result);

            // Insert fact and fire rules
            kieSession.insert(request);
            int rulesFired = kieSession.fireAllRules();

            log.debug("Fired {} validation rules", rulesFired);
        } finally {
            kieSession.dispose();
        }
    }
}
