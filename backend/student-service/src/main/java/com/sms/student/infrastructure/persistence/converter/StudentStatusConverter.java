package com.sms.student.infrastructure.persistence.converter;

import com.sms.student.domain.model.StudentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Attribute Converter for StudentStatus enum.
 * Converts enum values to their display names for database storage.
 */
@Converter(autoApply = true)
public class StudentStatusConverter implements AttributeConverter<StudentStatus, String> {

    @Override
    public String convertToDatabaseColumn(StudentStatus status) {
        if (status == null) {
            return null;
        }
        return status.getDisplayName();
    }

    @Override
    public StudentStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        for (StudentStatus status : StudentStatus.values()) {
            if (status.getDisplayName().equals(dbData)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown status: " + dbData);
    }
}
