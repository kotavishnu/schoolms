package com.school.student.rules;

import com.school.student.domain.model.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

/**
 * Service to execute Drools business rules
 * Validates student data against configured rules
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RuleExecutor {

    private final KieContainer kieContainer;

    /**
     * Validate student using Drools rules
     * Returns ValidationResult with any errors found
     */
    public ValidationResult validate(Student student) {
        log.debug("Executing validation rules for student");

        KieSession kieSession = kieContainer.newKieSession();
        ValidationResult result = new ValidationResult();

        try {
            kieSession.insert(student);
            kieSession.insert(result);
            int rulesFired = kieSession.fireAllRules();
            log.debug("Fired {} rules", rulesFired);
        } finally {
            kieSession.dispose();
        }

        if (result.hasErrors()) {
            log.warn("Validation failed with {} errors: {}", result.getErrors().size(), result.getErrors());
        } else {
            log.debug("Validation passed");
        }

        return result;
    }
}
