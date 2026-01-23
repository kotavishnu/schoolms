package com.school.student.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

/**
 * Drools Configuration
 *
 * Configures the Drools rules engine for business rule validation.
 * Loads all DRL files from src/main/resources/rules/ directory.
 *
 * Business Rules Implemented:
 * - BR-STU-001: Age range validation (3-18 years)
 * - BR-STU-002: Mobile uniqueness (checked in service layer)
 * - BR-STU-003: Email format validation
 * - BR-STU-004: Aadhaar format validation
 * - BR-STU-005: Name pattern validation
 * - BR-STU-006: Mobile format validation
 * - BR-STU-007: Editable fields constraint (enforced in API)
 */
@Configuration
public class DroolsConfig {

    private static final String RULES_PATH = "classpath*:rules/**/*.drl";

    /**
     * Create KieContainer bean that holds all compiled rules
     *
     * @return KieContainer with loaded rules
     * @throws IOException if DRL files cannot be read
     */
    @Bean
    public KieContainer kieContainer() throws IOException {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        // Load all DRL files
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(RULES_PATH);

        for (Resource resource : resources) {
            String path = "src/main/resources/" + resource.getFilename();
            kieFileSystem.write(path, kieServices.getResources()
                .newInputStreamResource(resource.getInputStream()));
        }

        // Build rules
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        if (kieBuilder.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
            throw new IllegalStateException("Drools rule compilation errors: " +
                kieBuilder.getResults().getMessages());
        }

        KieModule kieModule = kieBuilder.getKieModule();
        return kieServices.newKieContainer(kieModule.getReleaseId());
    }
}
