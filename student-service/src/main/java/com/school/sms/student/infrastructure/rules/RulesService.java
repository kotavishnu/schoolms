package com.school.sms.student.infrastructure.rules;

import com.school.sms.common.exception.BusinessRuleViolationException;
import com.school.sms.student.application.dto.CreateStudentRequest;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for executing Drools business rules.
 */
@Service
public class RulesService {

    private final KieSession kieSession;

    public RulesService(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    /**
     * Validate student registration using Drools rules.
     *
     * @param request the create student request
     * @throws BusinessRuleViolationException if validation fails
     */
    public void validateStudentRegistration(CreateStudentRequest request) {
        List<String> validationErrors = new ArrayList<>();

        try {
            // Set validation errors list as global
            kieSession.setGlobal("validationErrors", validationErrors);

            // Insert request object into working memory
            kieSession.insert(request);

            // Fire all rules
            kieSession.fireAllRules();

            // Check if any violations occurred
            if (!validationErrors.isEmpty()) {
                throw new BusinessRuleViolationException(
                    "Student registration validation failed",
                    validationErrors
                );
            }
        } finally {
            // Clean up working memory
            kieSession.dispose();
        }
    }
}
