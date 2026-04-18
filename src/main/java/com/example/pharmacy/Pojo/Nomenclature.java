package com.example.pharmacy.Pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.springframework.stereotype.Controller;

import java.util.List;

//todo json

@Entity
public class Nomenclature {
    public Nomenclature(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(nullable = true)
    private boolean isReceipt;
    @Column(nullable = true)
    private boolean isNarcotic;
    @Column(nullable = true)
    private boolean isPsycho;

    @OneToMany(mappedBy = "nomenclature", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("nomenclature")
    private List<ChequePosition> chequePositions;

    @OneToMany(mappedBy = "nomenclature", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("nomenclature")
    private List<Batch> batches;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atcManual_id")
    @JsonIgnoreProperties("nomenclatures")
    private AtcManual atcManual;

    //check
//    @ManyToMany(mappedBy = "nomenclatures", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonIgnoreProperties("nomenclatures")
//    private List<NomenclatureCategory> nomenclatureCategories;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "nomenclature_category",
            joinColumns = @JoinColumn(name = "nomenclature_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonIgnoreProperties("nomenclatures")
    private List<NomenclatureCategory> nomenclatureCategories;


    @OneToMany(mappedBy = "nomenclature", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("nomenclature")
    private List<MarkingCode> markingCodes;

    public List<MarkingCode> getMarkingCodes() {
        return markingCodes;
    }

    public void setMarkingCodes(List<MarkingCode> markingCodes) {
        this.markingCodes = markingCodes;
    }

    public List<NomenclatureCategory> getNomenclatureCategories() {
        return nomenclatureCategories;
    }

    public void setNomenclatureCategories(List<NomenclatureCategory> nomenclatureCategories) {
        this.nomenclatureCategories = nomenclatureCategories;
    }

    public AtcManual getAtcManual() {
        return atcManual;
    }

    public void setAtcManual(AtcManual atcManual) {
        this.atcManual = atcManual;
    }

    public List<Batch> getBatches() {
        return batches;
    }

    public void setBatches(List<Batch> batches) {
        this.batches = batches;
    }

    public List<ChequePosition> getChequePositions() {
        return chequePositions;
    }

    public void setChequePositions(List<ChequePosition> chequePositions) {
        this.chequePositions = chequePositions;
    }

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
