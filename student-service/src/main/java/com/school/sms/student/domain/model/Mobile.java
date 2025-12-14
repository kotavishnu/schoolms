package com.school.sms.student.domain.model;

import lombok.Value;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Value object representing a mobile phone number.
 * Must contain 10-15 digits only.
 * Immutable and validated.
 */
@Value
public class Mobile implements Serializable {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[0-9]{10,15}$");

    String number;

    private Mobile(String number) {
        this.number = number;
    }

    /**
     * Factory method to create a Mobile with validation.
     *
     * @param number the mobile number string
     * @return validated Mobile instance
     * @throws IllegalArgumentException if number is invalid
     */
    public static Mobile of(String number) {
        validateMobile(number);
        return new Mobile(number);
    }

    private static void validateMobile(String number) {
        if (number == null || number.trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number cannot be null or blank");
        }

        if (!MOBILE_PATTERN.matcher(number).matches()) {
            throw new IllegalArgumentException(
                String.format("Invalid mobile number format: %s. Must contain 10-15 digits only", number)
            );
        }
    }

    @Override
    public String toString() {
        return number;
    }
}
