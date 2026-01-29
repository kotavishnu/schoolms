package com.school.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class StudentServiceApplication {

    public static void main(String[] args) {
        // Set UTC timezone explicitly
        System.setProperty("user.timezone", "UTC");
        SpringApplication.run(StudentServiceApplication.class, args);
    }
}
