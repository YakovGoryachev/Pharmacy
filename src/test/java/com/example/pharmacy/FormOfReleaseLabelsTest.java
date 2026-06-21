package com.example.pharmacy;

import com.example.pharmacy.util.FormOfReleaseLabels;
import com.example.pharmacy.util.ProductTypeLabels;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormOfReleaseLabelsTest {

    @Test
    void label_returnsRussianForEnglishCode() {
        assertEquals("Таблетки", FormOfReleaseLabels.label("TABLET"));
        assertEquals("Упаковка", FormOfReleaseLabels.label("PACK"));
    }

    @Test
    void label_handlesLegacyRussianShortCodes() {
        assertEquals("Таблетки", FormOfReleaseLabels.label("таб."));
    }

    @Test
    void canonical_mapsLegacyToEnglishCode() {
        assertEquals("TABLET", FormOfReleaseLabels.canonical("таб."));
        assertEquals("TABLET", FormOfReleaseLabels.canonical("TABLET"));
    }

    @Test
    void productTypeLabel_returnsRussian() {
        assertEquals("Лекарственный препарат", ProductTypeLabels.label("MEDICINE"));
        assertEquals("ЛС", ProductTypeLabels.shortLabel("MEDICINE"));
    }
}
