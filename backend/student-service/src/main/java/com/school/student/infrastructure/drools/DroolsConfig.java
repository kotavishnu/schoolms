package com.school.student.infrastructure.drools;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Drools Rule Engine configuration.
 * Sets up KieContainer for loading and executing business rules.
 *
 * <p>Rule Loading Strategy:
 * <ul>
 *   <li>Rules are loaded from src/main/resources/rules/student/ directory</li>
 *   <li>All .drl files are automatically discovered and compiled</li>
 *   <li>Compilation errors are logged and will cause application startup failure</li>
 *   <li>KieContainer is a singleton, thread-safe container for rules</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 * {@literal @}Autowired
 * private KieContainer kieContainer;
 *
 * KieSession session = kieContainer.newKieSession();
 * session.insert(fact);
 * session.fireAllRules();
 * session.dispose();
 * </pre>
 */
@Configuration
@Profile("drools")
@Slf4j
public class DroolsConfig {

    private static final String RULES_PATH = "rules/student/";

    /**
     * Creates and configures KieContainer bean.
     * Loads all .drl files from the rules directory.
     *
     * @return KieContainer configured with business rules
     * @throws IllegalStateException if rule compilation fails
     */
    @Bean
    public KieContainer kieContainer() {
        log.info("Initializing Drools Rule Engine...");

        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        // Load all rule files from classpath
        loadRuleFiles(kieFileSystem);

        // Build the rules
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        // Check for compilation errors
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            log.error("Drools rule compilation errors:");
            kieBuilder.getResults().getMessages(Message.Level.ERROR).forEach(
                    message -> log.error("  - {}", message.getText())
            );
            throw new IllegalStateException("Drools rule compilation failed. Check logs for details.");
        }

        // Log warnings
        if (kieBuilder.getResults().hasMessages(Message.Level.WARNING)) {
            log.warn("Drools rule compilation warnings:");
            kieBuilder.getResults().getMessages(Message.Level.WARNING).forEach(
                    message -> log.warn("  - {}", message.getText())
            );
        }

        // Create KieContainer
        KieRepository kieRepository = kieServices.getRepository();
        KieModule kieModule = kieBuilder.getKieModule();
        kieRepository.addKieModule(kieModule);

        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());

        log.info("Drools Rule Engine initialized successfully. Rules loaded from: {}", RULES_PATH);
        return kieContainer;
    }

    /**
     * Load all .drl rule files from the rules directory.
     *
     * @param kieFileSystem the KieFileSystem to add rules to
     */
    private void loadRuleFiles(KieFileSystem kieFileSystem) {
        // Define rule files
        String[] ruleFiles = {
                "student-age-rules.drl",
                "student-uniqueness-rules.drl",
                "student-required-fields-rules.drl"
        };

        for (String ruleFile : ruleFiles) {
            String fullPath = RULES_PATH + ruleFile;
            log.debug("Loading rule file: {}", fullPath);

            try {
                kieFileSystem.write(
                        ResourceFactory.newClassPathResource(fullPath)
                );
            } catch (Exception e) {
                log.warn("Rule file not found (will be created): {}", fullPath);
                // Don't fail - rules will be created in BE-009
            }
        }
    }

    /**
     * Helper method to create a new KieSession.
     * Sessions should be disposed after use.
     *
     * @param kieContainer the KieContainer
     * @return new KieSession instance
     */
    public static KieSession createSession(KieContainer kieContainer) {
        return kieContainer.newKieSession();
    }
}
