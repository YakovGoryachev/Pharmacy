package com.example.pharmacy.Pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cheques")
public class Cheque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numberCheque;

    @Column(name = "fiscal_number", unique = true)
    private String fiscalNumber;

    private Integer totalAmount;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private Boolean isReturned = false;

    @CreationTimestamp
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id")
    @JsonIgnoreProperties("cheques")
    private Pharmacy pharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("cheques")
    private User user;

    @OneToMany(mappedBy = "cheque", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("cheque")
    private List<ChequePosition> chequePositions = new ArrayList<>();

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

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Boolean getIsReturned() {
        return isReturned;
    }

    public void setIsReturned(Boolean returned) {
        isReturned = returned;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ChequePosition> getChequePositions() {
        return chequePositions;
    }

    public void setChequePositions(List<ChequePosition> chequePositions) {
        this.chequePositions = chequePositions;
    }
}
