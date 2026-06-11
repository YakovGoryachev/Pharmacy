package com.example.pharmacy.DTO;

import java.util.List;
import java.util.Map;

public record PharmacyAnalyticsDto(
        Map<String, Object> metrics,
        DashboardChartsDto charts,
        String topCategoryName,
        Integer topCategoryRevenue,
        List<String> categoryLabels,
        List<Integer> categoryRevenues,
        String bestProductName,
        Integer bestProductRevenue,
        String worstProductName,
        Integer worstProductQty,
        Integer writeOffQty
) {
}
