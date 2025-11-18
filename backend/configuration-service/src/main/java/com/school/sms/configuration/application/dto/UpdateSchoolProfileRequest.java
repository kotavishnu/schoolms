package com.school.sms.configuration.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating school profile.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSchoolProfileRequest {

    @NotBlank(message = "School name is required")
    @Size(max = 200, message = "School name must not exceed 200 characters")
    private String schoolName;

    @NotBlank(message = "School code is required")
    @Pattern(regexp = "^[A-Z0-9]{3,20}$", message = "School code must be 3-20 alphanumeric characters")
    private String schoolCode;

    @Size(max = 500, message = "Logo path must not exceed 500 characters")
    private String logoPath;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Pattern(regexp = "^[0-9+()-]{10,15}$", message = "Phone must be 10-15 characters")
    private String phone;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
}
