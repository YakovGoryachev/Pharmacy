package com.example.pharmacy.Pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "cheque_positions")
public class ChequePosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;
    private Integer cost;
    private Integer sumOfPosition;

    @CreationTimestamp
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cheque_id")
    @JsonIgnoreProperties("chequePositions")
    private Cheque cheque;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nomenclature_id")
    @JsonIgnoreProperties("chequePositions")
    private Nomenclature nomenclature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    @JsonIgnoreProperties("chequePositions")
    private Batch batch;

    @OneToOne(mappedBy = "chequePosition", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("chequePosition")
    private MarkingCode markingCode;

    @OneToOne(mappedBy = "chequePosition", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("chequePosition")
    private Prescription prescription;

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

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getSumOfPosition() {
        return sumOfPosition;
    }

    public void setSumOfPosition(Integer sumOfPosition) {
        this.sumOfPosition = sumOfPosition;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Cheque getCheque() {
        return cheque;
    }

    public void setCheque(Cheque cheque) {
        this.cheque = cheque;
    }

    public Nomenclature getNomenclature() {
        return nomenclature;
    }

    public void setNomenclature(Nomenclature nomenclature) {
        this.nomenclature = nomenclature;
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public MarkingCode getMarkingCode() {
        return markingCode;
    }

    public void setMarkingCode(MarkingCode markingCode) {
        this.markingCode = markingCode;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }
}
