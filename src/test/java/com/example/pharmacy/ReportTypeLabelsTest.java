package com.example.pharmacy;

import com.example.pharmacy.util.ReportTypeLabels;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportTypeLabelsTest {

    @Test
    void label_returnsRussianForEnglishCode() {
        assertEquals("Остатки по партиям", ReportTypeLabels.label("STOCK"));
        assertEquals("Продажи", ReportTypeLabels.label("SALES"));
    }
}
