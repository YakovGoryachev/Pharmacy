package com.example.pharmacy.Pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String formType;
    private String patientName;
    private LocalDate patientBirthDate;
    private String doctorName;
    private String prescriptionNumber;
    private String prescriptionSeries;
    private LocalDate prescriptionDate;
    private String lpuCode;
    private String icd10Code;
    private String citizenCategoryCode;
    private String status;
    private LocalDate validUntil;
    private String preparedBy;
    private String checkedBy;
    private String dispensedBy;

    @CreationTimestamp
    private Instant createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cheque_position_id", unique = true, nullable = false)
    @JsonIgnoreProperties("prescription")
    private ChequePosition chequePosition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public LocalDate getPatientBirthDate() {
        return patientBirthDate;
    }

    public void setPatientBirthDate(LocalDate patientBirthDate) {
        this.patientBirthDate = patientBirthDate;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPrescriptionNumber() {
        return prescriptionNumber;
    }

    public void setPrescriptionNumber(String prescriptionNumber) {
        this.prescriptionNumber = prescriptionNumber;
    }

    public String getPrescriptionSeries() {
        return prescriptionSeries;
    }

    public void setPrescriptionSeries(String prescriptionSeries) {
        this.prescriptionSeries = prescriptionSeries;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public String getLpuCode() {
        return lpuCode;
    }

    public void setLpuCode(String lpuCode) {
        this.lpuCode = lpuCode;
    }

    public String getIcd10Code() {
        return icd10Code;
    }

    public void setIcd10Code(String icd10Code) {
        this.icd10Code = icd10Code;
    }

    public String getCitizenCategoryCode() {
        return citizenCategoryCode;
    }

    public void setCitizenCategoryCode(String citizenCategoryCode) {
        this.citizenCategoryCode = citizenCategoryCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public String getPreparedBy() {
        return preparedBy;
    }

    public void setPreparedBy(String preparedBy) {
        this.preparedBy = preparedBy;
    }

    public String getCheckedBy() {
        return checkedBy;
    }

    public void setCheckedBy(String checkedBy) {
        this.checkedBy = checkedBy;
    }

    public String getDispensedBy() {
        return dispensedBy;
    }

    public void setDispensedBy(String dispensedBy) {
        this.dispensedBy = dispensedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public ChequePosition getChequePosition() {
        return chequePosition;
    }

    public void setChequePosition(ChequePosition chequePosition) {
        this.chequePosition = chequePosition;
    }
}
