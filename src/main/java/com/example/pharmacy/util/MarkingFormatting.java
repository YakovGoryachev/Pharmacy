package com.example.pharmacy.util;

import org.springframework.stereotype.Component;

@Component("marking")
public class MarkingFormatting {

    public String mdlpStatus(String code) {
        return MdlpStatusLabels.label(code);
    }

    public String stockStatus(String code) {
        return MarkingCodeStatusLabels.label(code);
    }
}
