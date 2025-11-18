package com.school.sms.configuration.presentation.controller;

import com.school.sms.configuration.application.dto.SchoolProfileResponse;
import com.school.sms.configuration.application.dto.UpdateSchoolProfileRequest;
import com.school.sms.configuration.application.service.SchoolProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for school profile management.
 */
@RestController
@RequestMapping("/api/v1/configurations/school-profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "School Profile", description = "School profile management API")
public class SchoolProfileController {

    private final SchoolProfileService service;

    private static final String USER_ID_HEADER = "X-User-ID";
    private static final String DEFAULT_USER_ID = "system";

    /**
     * Get the school profile.
     */
    @GetMapping
    @Operation(summary = "Get school profile",
               description = "Retrieves the school profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "School profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SchoolProfileResponse.class))),
        @ApiResponse(responseCode = "404", description = "School profile not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<SchoolProfileResponse> getSchoolProfile() {
        log.debug("Received request to get school profile");

        SchoolProfileResponse response = service.getSchoolProfile();

        return ResponseEntity.ok(response);
    }

    /**
     * Update the school profile.
     */
    @PutMapping
    @Operation(summary = "Update school profile",
               description = "Updates the school profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "School profile updated successfully",
                    content = @Content(schema = @Schema(implementation = SchoolProfileResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "School profile not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Optimistic locking failure",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<SchoolProfileResponse> updateSchoolProfile(
            @Valid @RequestBody UpdateSchoolProfileRequest request,
            @RequestHeader(value = USER_ID_HEADER, defaultValue = DEFAULT_USER_ID) String userId) {

        log.info("Received request to update school profile");

        SchoolProfileResponse response = service.updateSchoolProfile(request, userId);

        return ResponseEntity.ok(response);
    }
}
