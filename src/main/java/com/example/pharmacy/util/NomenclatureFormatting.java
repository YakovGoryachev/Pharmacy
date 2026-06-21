package com.example.pharmacy.util;

import org.springframework.stereotype.Component;

@Component("nomenclature")
public class NomenclatureFormatting {

    public String formOfRelease(String code) {
        return FormOfReleaseLabels.label(code);
    }

    public String shortFormOfRelease(String code) {
        return FormOfReleaseLabels.shortLabel(code);
    }

    public String canonicalForm(String code) {
        return FormOfReleaseLabels.canonical(code);
    }

    public String productType(String code) {
        return ProductTypeLabels.label(code);
    }

    public String productTypeShort(String code) {
        return ProductTypeLabels.shortLabel(code);
    }

    public String dosageLine(Integer dosage, String unit) {
        if (dosage == null) {
            return unit != null && !unit.isBlank() ? unit.trim() : "—";
        }
        if (unit == null || unit.isBlank()) {
            return String.valueOf(dosage);
        }
        return dosage + " " + unit.trim();
    }
}
