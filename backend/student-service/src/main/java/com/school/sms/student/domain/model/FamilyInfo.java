package com.school.sms.student.domain.model;

import lombok.Value;

/**
 * Value object representing a student's family information.
 *
 * <p>This is an immutable object containing family member details.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Value
public class FamilyInfo {

    String fathersName;
    String mothersName;

    /**
     * Creates a FamilyInfo instance.
     *
     * @param fathersName the father's/guardian's name (optional)
     * @param mothersName the mother's name (optional)
     */
    public FamilyInfo(String fathersName, String mothersName) {
        this.fathersName = fathersName;
        this.mothersName = mothersName;
    }
}
