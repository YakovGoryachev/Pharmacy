package Pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Date;

public class MarkingCode {
    public MarkingCode(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String gtin;
    private String serialNumber;
    private Date expiryDate;
    private String status;
    @CreationTimestamp
    private Instant withdrawnAt;
    private String mdlpStatus;
    @CreationTimestamp
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nomenclature_id")
    @JsonIgnoreProperties("markingCodes")
    private Nomenclature nomenclature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    @JsonIgnoreProperties("markingCodes")
    private Batch batch;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chequePosition_id")
    @JsonIgnoreProperties("markingCode")
    private ChequePosition chequePosition;

    public ChequePosition getChequePosition() {
        return chequePosition;
    }

    public void setChequePosition(ChequePosition chequePosition) {
        this.chequePosition = chequePosition;
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public Nomenclature getNomenclature() {
        return nomenclature;
    }

    public void setNomenclature(Nomenclature nomenclature) {
        this.nomenclature = nomenclature;
    }

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

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
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
}
