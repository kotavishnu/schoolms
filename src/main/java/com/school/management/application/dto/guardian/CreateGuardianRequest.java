package com.school.management.application.dto.guardian;

import com.school.management.domain.student.Relationship;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new guardian
 *
 * @author School Management System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGuardianRequest {

    @NotNull(message = "Relationship is required")
    private Relationship relationship;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be exactly 10 digits")
    private String mobile;

    @Email(message = "Email must be valid")
    private String email;

    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    private String occupation;

    @NotNull(message = "Primary status is required")
    private Boolean isPrimary = false;
}
