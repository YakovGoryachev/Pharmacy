package com.example.pharmacy.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

//todo connectinos, JSON(record)

public class NomenclatureDto {
    public NomenclatureDto(){

    }

    public NomenclatureDto(Long id, String mnn, String brandName, String formOfRelease, Integer dosage, Integer qtyInPack, String manufacturer, String country, String barcode, Integer minStockLevel, Boolean Receipt, Boolean Narcotic, Boolean Psycho) {
        this.id = id;
        this.mnn = mnn;
        this.brandName = brandName;
        this.formOfRelease = formOfRelease;
        this.dosage = dosage;
        this.qtyInPack = qtyInPack;
        this.manufacturer = manufacturer;
        this.country = country;
        this.barcode = barcode;
        this.minStockLevel = minStockLevel;
        this.Receipt = Receipt;
        this.Narcotic = Narcotic;
        this.Psycho = Psycho;
    }

    private Long id;
    private Long atxId;
    private String mnn;
    private String atxCode;
    private String brandName;
    private String formOfRelease;
    private Integer dosage;
    private String dosageUnit;
    private Integer qtyInPack;
    private String manufacturer;
    private String country;
    private String barcode;
    private Integer price;
    private record storageConditions(){}
    private Integer minStockLevel;
    private Boolean Receipt;
    private Boolean Narcotic;
    private Boolean Psycho;

    private Long productCategoryId;
    private String productCategoryName;

    public String getDosageUnit() {
        return dosageUnit;
    }

    public void setDosageUnit(String dosageUnit) {
        this.dosageUnit = dosageUnit;
    }

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }

    public Long getAtxId() {
        return atxId;
    }

    public void setAtxId(Long atxId) {
        this.atxId = atxId;
    }

    public Long getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Long productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getMnn() {
        return mnn;
    }

    public void setMnn(String mnn) {
        this.mnn = mnn;
    }

    public String getAtxCode() {
        return atxCode;
    }

    public void setAtxCode(String atxCode) {
        this.atxCode = atxCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getQtyInPack() {
        return qtyInPack;
    }

    public void setQtyInPack(Integer qtyInPack) {
        this.qtyInPack = qtyInPack;
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

    public Boolean getReceipt() {
        return Receipt;
    }

    public void setReceipt(Boolean receipt) {
        Receipt = receipt;
    }

    public Boolean getNarcotic() {
        return Narcotic;
    }

    public void setNarcotic(Boolean narcotic) {
        Narcotic = narcotic;
    }

    public Boolean getPsycho() {
        return Psycho;
    }

    public void setPsycho(Boolean psycho) {
        Psycho = psycho;
    }
}
