package com.example.pharmacy.util;

import com.example.pharmacy.Pojo.ProductType;

public final class ProductTypeLabels {

    private ProductTypeLabels() {
    }

    public static String label(String code) {
        if (code == null || code.isBlank()) {
            return "—";
        }
        try {
            return ProductType.valueOf(code.trim().toUpperCase()).getLabel();
        } catch (IllegalArgumentException ex) {
            return code.trim();
        }
    }

    public static String shortLabel(String code) {
        if (code == null || code.isBlank()) {
            return "—";
        }
        return switch (code.trim().toUpperCase()) {
            case "MEDICINE" -> "ЛС";
            case "PARAPHARMACY" -> "Парафарма";
            case "COSMETIC" -> "Косметика";
            case "HYGIENE" -> "Гигиена";
            case "MEDICAL_DEVICE" -> "Медизделие";
            case "OTHER" -> "Прочее";
            default -> code.trim();
        };
    }
}
