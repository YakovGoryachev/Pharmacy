package com.example.pharmacy.util;

import java.util.Map;

public final class StorageZoneLabels {

    private static final Map<String, String> LABELS = Map.of(
            "Зал", "Зал",
            "ROOM_TEMP", "Комнатная температура",
            "FRIDGE", "Холодильник (2–8°C)",
            "DARK", "Тёмное место",
            "FREEZER", "Морозильная камера"
    );

    private StorageZoneLabels() {
    }

    public static String label(String code) {
        if (code == null || code.isBlank()) {
            return "—";
        }
        return LABELS.getOrDefault(code.trim(), code);
    }
}
