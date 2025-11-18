package com.school.sms.student.presentation.controller;

import com.school.sms.student.application.service.StudentStatisticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for StudentStatisticsController.
 */
@WebMvcTest(StudentStatisticsController.class)
@DisplayName("Student Statistics Controller Tests")
class StudentStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentStatisticsService statisticsService;

    @Test
    @DisplayName("Should get student statistics successfully")
    void shouldGetStudentStatisticsSuccessfully() throws Exception {
        // Given
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalStudents", 250L);
        statistics.put("activeStudents", 235L);
        statistics.put("inactiveStudents", 15L);
        statistics.put("averageAge", 12.5);

        Map<String, Long> ageDistribution = new HashMap<>();
        ageDistribution.put("3-6", 45L);
        ageDistribution.put("7-10", 85L);
        ageDistribution.put("11-14", 95L);
        ageDistribution.put("15-18", 25L);
        statistics.put("ageDistribution", ageDistribution);

        Map<String, Long> casteDistribution = new HashMap<>();
        casteDistribution.put("General", 120L);
        casteDistribution.put("OBC", 80L);
        statistics.put("casteDistribution", casteDistribution);

        when(statisticsService.getStatistics()).thenReturn(statistics);

        // When & Then
        mockMvc.perform(get("/students/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStudents").value(250))
                .andExpect(jsonPath("$.activeStudents").value(235))
                .andExpect(jsonPath("$.inactiveStudents").value(15))
                .andExpect(jsonPath("$.averageAge").value(12.5))
                .andExpect(jsonPath("$.ageDistribution.3-6").value(45))
                .andExpect(jsonPath("$.casteDistribution.General").value(120));
    }
}
