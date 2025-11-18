package com.school.sms.student.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Value object representing a physical address.
 * This is an embeddable entity used within the Student entity.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Address {

    @Column(name = "street", length = 200, nullable = false)
    private String street;

    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @Column(name = "state", length = 100, nullable = false)
    private String state;

    @Column(name = "pin_code", length = 6, nullable = false)
    private String pinCode;

    @Column(name = "country", length = 50, nullable = false)
    @Builder.Default
    private String country = "India";

    /**
     * Returns the full address as a formatted string.
     *
     * @return formatted address string
     */
    public String getFullAddress() {
        return String.format("%s, %s, %s - %s, %s",
            street, city, state, pinCode, country);
    }
}
