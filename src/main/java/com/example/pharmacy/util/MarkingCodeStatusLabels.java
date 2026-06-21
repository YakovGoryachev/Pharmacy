package com.example.pharmacy.util;

import java.util.Map;

public final class MarkingCodeStatusLabels {

    private static final Map<String, String> LABELS = Map.of(
            "IN_STOCK", "На складе",
            "RESERVED", "Зарезервирован",
            "DISPOSED", "Продан",
            "RETURNED", "Возвращён"
    );

    private MarkingCodeStatusLabels() {
    }

    public static String label(String code) {
        if (code == null || code.isBlank()) {
            return "—";
        }
        return LABELS.getOrDefault(code.trim(), code);
    }
}
