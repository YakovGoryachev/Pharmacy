package com.example.pharmacy.Pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "stock_transfers")
public class StockTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;
    private String waybillNumber;
    private LocalDate transferDate;

    @CreationTimestamp
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_pharmacy_id", nullable = false)
    @JsonIgnoreProperties("outgoingTransfers")
    private Pharmacy fromPharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_pharmacy_id", nullable = false)
    @JsonIgnoreProperties("incomingTransfers")
    private Pharmacy toPharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    @JsonIgnoreProperties("transfers")
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("transfers")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Pharmacy getFromPharmacy() {
        return fromPharmacy;
    }

    public void setFromPharmacy(Pharmacy fromPharmacy) {
        this.fromPharmacy = fromPharmacy;
    }

    public Pharmacy getToPharmacy() {
        return toPharmacy;
    }

    public void setToPharmacy(Pharmacy toPharmacy) {
        this.toPharmacy = toPharmacy;
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
