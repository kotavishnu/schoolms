package com.school.sms.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Student Service.
 */
@SpringBootApplication(scanBasePackages = {
    "com.school.sms.student",
    "com.school.sms.common"
})
@EnableCaching
@EnableJpaAuditing
public class StudentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentServiceApplication.class, args);
    }
}
