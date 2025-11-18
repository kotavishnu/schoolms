package com.school.sms.student.presentation.controller;

import com.school.sms.student.application.service.StudentStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for Student Statistics.
 * Provides endpoints for aggregated student data and analytics.
 */
@RestController
@RequestMapping("/students/statistics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Student Statistics", description = "APIs for student statistics and analytics")
public class StudentStatisticsController {

    private final StudentStatisticsService statisticsService;

    /**
     * Gets comprehensive student statistics.
     *
     * @return map containing various statistics
     */
    @GetMapping
    @Operation(summary = "Get student statistics",
               description = "Retrieve aggregated statistics including total count, age distribution, and caste distribution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("Getting student statistics");
        Map<String, Object> statistics = statisticsService.getStatistics();
        return ResponseEntity.ok(statistics);
    }
}
