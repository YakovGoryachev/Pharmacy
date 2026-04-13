package com.example.pharmacy.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

//todo connectinos, JSON(record)

public class NomenclatureDto {
    public NomenclatureDto(){

    }

    public NomenclatureDto(Long id, String MNN, String brandName, String formOfRelease, Integer dosage, Integer quantityInPack, String manufacturer, String country, String barcode, Integer minStockLevel, boolean isReceipt, boolean isNarcotic, boolean isPsycho) {
        this.id = id;
        this.MNN = MNN;
        this.brandName = brandName;
        this.formOfRelease = formOfRelease;
        this.dosage = dosage;
        this.quantityInPack = quantityInPack;
        this.manufacturer = manufacturer;
        this.country = country;
        this.barcode = barcode;
        this.minStockLevel = minStockLevel;
        this.isReceipt = isReceipt;
        this.isNarcotic = isNarcotic;
        this.isPsycho = isPsycho;
    }

    private Long id;
    private String MNN;
    private String brandName;
    private String formOfRelease;
    private Integer dosage;
    private Integer quantityInPack;
    private String manufacturer;
    private String country;
    private String barcode;
    private record storageConditions(){}
    private Integer minStockLevel;
    private boolean isReceipt;
    private boolean isNarcotic;
    private boolean isPsycho;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMNN() {
        return MNN;
    }

    public void setMNN(String MNN) {
        this.MNN = MNN;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getFormOfRelease() {
        return formOfRelease;
    }

    public void setFormOfRelease(String formOfRelease) {
        this.formOfRelease = formOfRelease;
    }

    public Integer getDosage() {
        return dosage;
    }

    public void setDosage(Integer dosage) {
        this.dosage = dosage;
    }

    public Integer getQuantityInPack() {
        return quantityInPack;
    }

    public void setQuantityInPack(Integer quantityInPack) {
        this.quantityInPack = quantityInPack;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Integer getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(Integer minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public boolean isReceipt() {
        return isReceipt;
    }

    public void setReceipt(boolean receipt) {
        isReceipt = receipt;
    }

    public boolean isNarcotic() {
        return isNarcotic;
    }

    public void setNarcotic(boolean narcotic) {
        isNarcotic = narcotic;
    }

    public boolean isPsycho() {
        return isPsycho;
    }

    public void setPsycho(boolean psycho) {
        isPsycho = psycho;
    }
}
