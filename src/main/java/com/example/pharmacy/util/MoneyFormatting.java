package com.example.pharmacy.util;

import org.springframework.stereotype.Component;

@Component("money")
public class MoneyFormatting {

    public String rubles(Integer kopecks) {
        return MoneyUtils.formatRubles(kopecks);
    }

    public Double asRubles(Integer kopecks) {
        return MoneyUtils.toRubles(kopecks);
    }
}
