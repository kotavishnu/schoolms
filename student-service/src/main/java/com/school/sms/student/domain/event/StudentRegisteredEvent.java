package com.school.sms.student.domain.event;

import lombok.Value;
import java.time.LocalDateTime;

/**
 * Domain event emitted when a student is registered.
 * Immutable event for audit and future event sourcing.
 */
@Value
public class StudentRegisteredEvent {
    String studentId;
    String firstName;
    String lastName;
    String mobile;
    LocalDateTime registeredAt;

    public static StudentRegisteredEvent of(String studentId, String firstName,
                                           String lastName, String mobile) {
        return new StudentRegisteredEvent(
            studentId,
            firstName,
            lastName,
            mobile,
            LocalDateTime.now()
        );
    }
}
