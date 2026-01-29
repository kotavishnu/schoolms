package com.school.student.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Drools rule engine configuration
 * Loads DRL files from classpath and creates KieContainer
 */
@Configuration
@Slf4j
public class DroolsConfig {

    /**
     * Create KieContainer bean for rule execution
     * Loads all .drl files from resources/rules/student/
     */
    @Bean
    public KieContainer kieContainer() {
        log.info("Initializing Drools KieContainer");

        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        // Load rule files from classpath
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules/student/student-age-validation.drl"));
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules/student/mobile-validation.drl"));
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules/student/aadhaar-validation.drl"));

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        // Check for errors
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            log.error("Drools compilation errors:");
            kieBuilder.getResults().getMessages(Message.Level.ERROR)
                .forEach(msg -> log.error("  - {}", msg.getText()));
            throw new RuntimeException("Drools rules compilation failed");
        }

        KieModule kieModule = kieBuilder.getKieModule();
        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());

        log.info("Drools KieContainer initialized successfully");
        return kieContainer;
    }
}
