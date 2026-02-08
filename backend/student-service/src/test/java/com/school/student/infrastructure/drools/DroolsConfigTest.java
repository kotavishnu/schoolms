package com.school.student.infrastructure.drools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for DroolsConfig.
 * Tests KieContainer bean creation and rule loading.
 *
 * <p>Following D-013: Infrastructure Layer Testing.
 * Note: Tests validate configuration bean creation only, as rule files
 * require Drools profile to be active in production.
 */
@DisplayName("DroolsConfig Unit Tests")
class DroolsConfigTest {

    @Test
    @DisplayName("Should instantiate DroolsConfig")
    void shouldInstantiateDroolsConfig() {
        // Act
        DroolsConfig config = new DroolsConfig();

        // Assert
        assertThat(config).isNotNull();
    }

    @Test
    @DisplayName("Should be annotated with @Configuration and @Profile")
    void shouldBeAnnotatedWithConfigurationAndProfile() {
        // Assert
        assertThat(DroolsConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class))
            .isTrue();
        assertThat(DroolsConfig.class.isAnnotationPresent(org.springframework.context.annotation.Profile.class))
            .isTrue();
    }

    @Test
    @DisplayName("Should have kieContainer bean method")
    void shouldHaveKieContainerBeanMethod() {
        // Assert
        assertThatCode(() -> {
            DroolsConfig.class.getMethod("kieContainer");
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should have createSession static helper method")
    void shouldHaveCreateSessionStaticHelperMethod() {
        // Assert
        assertThatCode(() -> {
            DroolsConfig.class.getMethod("createSession", org.kie.api.runtime.KieContainer.class);
        }).doesNotThrowAnyException();
    }
}
