package Pojo;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

//todo connectinos code pharmacy code user
//todo constraints


@Entity
public class Cheque {
    public Cheque(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numberCheque;
    @Column(name = "fiscal_number", nullable = false, unique = true)
    private String fiscalNumber;
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    @CreationTimestamp
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
