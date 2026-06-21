package com.example.pharmacy.Pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "marking_codes", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class MarkingCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String gtin;
    private String serialNumber;
    private LocalDate expiryDate;
    private String status;

    private Instant withdrawnAt;
    private Long disposalDocumentId;
    private String mdlpStatus;

    @CreationTimestamp
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    @JsonIgnoreProperties("markingCodes")
    private Batch batch;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cheque_position_id")
    @JsonIgnoreProperties("markingCode")
    private ChequePosition chequePosition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGtin() {
        return gtin;
    }

    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getWithdrawnAt() {
        return withdrawnAt;
    }

    public void setWithdrawnAt(Instant withdrawnAt) {
        this.withdrawnAt = withdrawnAt;
    }

    public Long getDisposalDocumentId() {
        return disposalDocumentId;
    }

    public void setDisposalDocumentId(Long disposalDocumentId) {
        this.disposalDocumentId = disposalDocumentId;
    }

    public String getMdlpStatus() {
        return mdlpStatus;
    }

    public void setMdlpStatus(String mdlpStatus) {
        this.mdlpStatus = mdlpStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public ChequePosition getChequePosition() {
        return chequePosition;
    }

    public void setChequePosition(ChequePosition chequePosition) {
        this.chequePosition = chequePosition;
    }
}
