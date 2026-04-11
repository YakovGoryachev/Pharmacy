package DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Date;

//todo connections

public class MarkingCodeDto {
    public MarkingCodeDto(){

    }

    public MarkingCodeDto(Long id, String code, String gtin, String serialNumber, Date expiryDate, String status, Instant withdrawnAt, String mdlpStatus, Instant createdAt) {
        this.id = id;
        this.code = code;
        this.gtin = gtin;
        this.serialNumber = serialNumber;
        this.expiryDate = expiryDate;
        this.status = status;
        this.withdrawnAt = withdrawnAt;
        this.mdlpStatus = mdlpStatus;
        this.createdAt = createdAt;
    }

    private Long id;
    private String code;
    private String gtin;
    private String serialNumber;
    private Date expiryDate;
    private String status;
    private Instant withdrawnAt;
    private String mdlpStatus;
    private Instant createdAt;

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
