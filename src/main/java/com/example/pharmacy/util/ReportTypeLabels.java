package com.example.pharmacy.util;

import java.util.Map;

public final class ReportTypeLabels {

    private static final Map<String, String> LABELS = Map.of(
            "STOCK", "Остатки по партиям",
            "SALES", "Продажи"
    );

    private ReportTypeLabels() {
    }

    public static String label(String code) {
        if (code == null || code.isBlank()) {
            return "—";
        }
        return LABELS.getOrDefault(code.trim().toUpperCase(), code.trim());
    }
}
