package com.example.pharmacy.DTO;

import java.io.Serializable;
import java.time.LocalDate;

public class PrescriptionFormDto implements Serializable {
    private String patientName;
    private LocalDate patientBirthDate;
    private String doctorName;
    private String prescriptionNumber;
    private String prescriptionSeries;
    private LocalDate prescriptionDate;
    private String lpuCode;

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

    public boolean isComplete() {
        return prescriptionNumber != null && !prescriptionNumber.isBlank()
                && prescriptionDate != null
                && lpuCode != null && !lpuCode.isBlank();
    }
}
