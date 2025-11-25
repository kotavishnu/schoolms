package com.school.sms.student.domain.event;

import com.school.sms.student.domain.model.StudentStatus;
import lombok.Value;
import java.time.LocalDateTime;

/**
 * Domain event emitted when a student's status changes.
 * Immutable event for audit and future event sourcing.
 */
@Value
public class StudentStatusChangedEvent {
    String studentId;
    StudentStatus oldStatus;
    StudentStatus newStatus;
    LocalDateTime changedAt;

    public static StudentStatusChangedEvent of(String studentId,
                                              StudentStatus oldStatus,
                                              StudentStatus newStatus) {
        return new StudentStatusChangedEvent(
            studentId,
            oldStatus,
            newStatus,
            LocalDateTime.now()
        );
    }
}
