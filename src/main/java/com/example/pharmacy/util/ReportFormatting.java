package com.example.pharmacy.util;

import org.springframework.stereotype.Component;

@Component("report")
public class ReportFormatting {

    public String typeLabel(String code) {
        return ReportTypeLabels.label(code);
    }
}
