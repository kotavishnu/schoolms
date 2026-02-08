package com.school.student.domain.model.valueobject;

import lombok.Value;

/**
 * Value Object representing guardian information for a student.
 * Immutable and enforces business rule BR-5 (at least one guardian required).
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-03
 */
@Value
public class GuardianInfo {

    String fathersName;
    String mothersName;

    /**
     * Factory method to create GuardianInfo with validation.
     * Business Rule BR-5: At least one guardian name is required.
     *
     * @param fathersName father's or primary guardian's name
     * @param mothersName mother's name
     * @return GuardianInfo instance
     * @throws IllegalArgumentException if both names are null/empty
     */
    public static GuardianInfo of(String fathersName, String mothersName) {
        boolean fatherEmpty = fathersName == null || fathersName.isBlank();
        boolean motherEmpty = mothersName == null || mothersName.isBlank();

        if (fatherEmpty && motherEmpty) {
            throw new IllegalArgumentException(
                "At least one guardian name (father or mother) is required (BR-5)"
            );
        }

        return new GuardianInfo(
            fatherEmpty ? null : fathersName.trim(),
            motherEmpty ? null : mothersName.trim()
        );
    }

    /**
     * Returns the primary guardian's name.
     * Prioritizes father's name, falls back to mother's name.
     *
     * @return primary guardian name
     */
    public String getPrimaryGuardian() {
        return fathersName != null ? fathersName : mothersName;
    }
}
