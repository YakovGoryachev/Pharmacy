package com.example.pharmacy.Pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class StorageConditions {

    @Column(name = "storage_temp_min")
    private Integer tempMinC;

    @Column(name = "storage_temp_max")
    private Integer tempMaxC;

    @Column(name = "storage_humidity")
    private Integer humidityPercent;

    @Column(name = "storage_light_protected")
    private Boolean lightProtected;

    @Column(name = "storage_notes", length = 500)
    private String notes;

    public Integer getTempMinC() {
        return tempMinC;
    }

    public void setTempMinC(Integer tempMinC) {
        this.tempMinC = tempMinC;
    }

    public Integer getTempMaxC() {
        return tempMaxC;
    }

    public void setTempMaxC(Integer tempMaxC) {
        this.tempMaxC = tempMaxC;
    }

    public Integer getHumidityPercent() {
        return humidityPercent;
    }

    public void setHumidityPercent(Integer humidityPercent) {
        this.humidityPercent = humidityPercent;
    }

    public Boolean getLightProtected() {
        return lightProtected;
    }

    public void setLightProtected(Boolean lightProtected) {
        this.lightProtected = lightProtected;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
