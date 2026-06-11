package com.example.pharmacy.DTO;

import java.util.List;

public record DashboardChartsDto(
        List<String> dayLabels,
        List<Integer> dayRevenue,
        List<String> paymentLabels,
        List<Integer> paymentAmounts,
        List<String> topProductLabels,
        List<Integer> topProductAmounts
) {
}
