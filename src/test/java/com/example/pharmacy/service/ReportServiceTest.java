package com.example.pharmacy.service;

import com.example.pharmacy.Service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ReportServiceTest {

    @Autowired ReportService reportService;

    @Test
    void dashboardReturnsMetrics() {
        Instant to = Instant.now();
        Instant from = to.minus(30, ChronoUnit.DAYS);
        Map<String, Object> metrics = reportService.dashboard(null, from, to);
        assertNotNull(metrics);
        assertTrue(metrics.containsKey("revenue"));
        assertTrue(metrics.containsKey("avgCheque"));
    }
}
