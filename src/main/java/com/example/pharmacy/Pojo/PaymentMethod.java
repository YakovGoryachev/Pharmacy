package com.example.pharmacy.Pojo;

import java.util.Arrays;
import java.util.List;

public enum PaymentMethod {
    CASH, BANK_CARD, ONLINE, QR, INSTALLMENT;

    public boolean isAvailableAtCashier() {
        return this != INSTALLMENT;
    }

    public static List<PaymentMethod> forCashier() {
        return Arrays.stream(values()).filter(PaymentMethod::isAvailableAtCashier).toList();
    }
}
