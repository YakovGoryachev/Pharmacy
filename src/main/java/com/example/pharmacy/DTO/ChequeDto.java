package com.example.pharmacy.DTO;

import com.example.pharmacy.Pojo.PaymentMethod;

import java.time.Instant;

//todo connections

public class ChequeDto {
    public ChequeDto(){

    }

    public ChequeDto(Long id, String numberCheque, String fiscalNumber, PaymentMethod paymentMethod, Instant createdAt) {
        this.id = id;
        this.numberCheque = numberCheque;
        this.fiscalNumber = fiscalNumber;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
    }

    private Long id;
    private String numberCheque;
    private String fiscalNumber;
    private PaymentMethod paymentMethod;
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumberCheque() {
        return numberCheque;
    }

    public void setNumberCheque(String numberCheque) {
        this.numberCheque = numberCheque;
    }

    public String getFiscalNumber() {
        return fiscalNumber;
    }

    public void setFiscalNumber(String fiscalNumber) {
        this.fiscalNumber = fiscalNumber;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
