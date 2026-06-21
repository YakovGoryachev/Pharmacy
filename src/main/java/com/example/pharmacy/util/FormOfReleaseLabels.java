package com.example.pharmacy.util;

import java.util.Map;

public final class FormOfReleaseLabels {

    private static final Map<String, String> LABELS = Map.ofEntries(
            Map.entry("TABLET", "Таблетки"),
            Map.entry("CAPSULE", "Капсулы"),
            Map.entry("SOLUTION", "Раствор"),
            Map.entry("OINTMENT", "Мазь / крем"),
            Map.entry("SYRUP", "Сироп"),
            Map.entry("DROPS", "Капли"),
            Map.entry("SPRAY", "Спрей"),
            Map.entry("POWDER", "Порошок"),
            Map.entry("INJECTION", "Раствор для инъекций"),
            Map.entry("PIECE", "Штука"),
            Map.entry("PACK", "Упаковка"),
            Map.entry("BOTTLE", "Флакон / бутылка"),
            Map.entry("TUBE", "Туба"),
            Map.entry("BOX", "Коробка"),
            Map.entry("SET", "Набор")
    );

    private static final Map<String, String> SHORT_LABELS = Map.ofEntries(
            Map.entry("TABLET", "таб."),
            Map.entry("CAPSULE", "капс."),
            Map.entry("SOLUTION", "р-р"),
            Map.entry("OINTMENT", "мазь"),
            Map.entry("SYRUP", "сироп"),
            Map.entry("DROPS", "капли"),
            Map.entry("SPRAY", "спрей"),
            Map.entry("POWDER", "пор."),
            Map.entry("INJECTION", "инъек."),
            Map.entry("PIECE", "шт."),
            Map.entry("PACK", "уп."),
            Map.entry("BOTTLE", "фл."),
            Map.entry("TUBE", "туба"),
            Map.entry("BOX", "кор."),
            Map.entry("SET", "набор")
    );

    private static final Map<String, String> ALIASES = Map.ofEntries(
            Map.entry("ТАБ.", "TABLET"),
            Map.entry("ТАБ", "TABLET"),
            Map.entry("таб.", "TABLET"),
            Map.entry("таб", "TABLET"),
            Map.entry("КАПС.", "CAPSULE"),
            Map.entry("капс.", "CAPSULE"),
            Map.entry("Р-Р", "SOLUTION"),
            Map.entry("р-р", "SOLUTION")
    );

    private FormOfReleaseLabels() {
    }

    public static String canonical(String code) {
        if (code == null || code.isBlank()) {
            return code;
        }
        String trimmed = code.trim();
        String upper = trimmed.toUpperCase();
        if (LABELS.containsKey(upper)) {
            return upper;
        }
        return ALIASES.getOrDefault(trimmed, ALIASES.getOrDefault(upper, trimmed));
    }

    public static String label(String code) {
        if (code == null || code.isBlank()) {
            return "—";
        }
        String canonical = canonical(code);
        String fromCanonical = LABELS.get(canonical);
        if (fromCanonical != null) {
            return fromCanonical;
        }
        return LABELS.getOrDefault(code.trim().toUpperCase(), code.trim());
    }

    public static String shortLabel(String code) {
        if (code == null || code.isBlank()) {
            return "";
        }
        String canonical = canonical(code);
        return SHORT_LABELS.getOrDefault(canonical, label(code));
    }
}
