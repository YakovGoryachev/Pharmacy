package com.example.pharmacy.util;

import java.util.Map;

public final class MdlpStatusLabels {

    private static final Map<String, String> LABELS = Map.of(
            "REGISTERED", "Зарегистрирован",
            "PENDING_SEND", "Ожидает отправки",
            "SENT", "Отправлен",
            "RETURNED", "Возврат передан"
    );

    private MdlpStatusLabels() {
    }

    public static String label(String code) {
        if (code == null || code.isBlank()) {
            return "—";
        }
        return LABELS.getOrDefault(code.trim(), code);
    }
}
