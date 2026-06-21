package com.example.pharmacy.Pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Nomenclature {
    public Nomenclature(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 32)
    private String productType = ProductType.MEDICINE.name();
    private String mnn;
    private String brandName;
    private String formOfRelease;
    private Integer dosage;
    private String dosageUnit;
    private Integer quantityInPack;
    private String manufacturer;
    private String country;
    private String barcode;
    private Integer price;
    @Embedded
    private StorageConditions storageConditions;
    private Integer minStockLevel;
    @Column(nullable = true, columnDefinition = "boolean default false")
    private Boolean marked = false;
    @Column(nullable = true)
    private Boolean receipt;
    @Column(nullable = true)
    private Boolean narcotic;
    @Column(nullable = true)
    private Boolean psychotropic;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "nomenclature_category",
            joinColumns = @JoinColumn(name = "nomenclature_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonIgnoreProperties("nomenclatures")
    private List<NomenclatureCategory> nomenclatureCategories;

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

    public String getProductType() {
        return productType != null ? productType : ProductType.MEDICINE.name();
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMnn() {
        return mnn;
    }

    public void setMnn(String mnn) {
        this.mnn = mnn;
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Boolean getReceipt() {
        return receipt;
    }

    public void setReceipt(Boolean receipt) {
        this.receipt = receipt;
    }

    public Boolean getNarcotic() {
        return narcotic;
    }

    public void setNarcotic(Boolean narcotic) {
        this.narcotic = narcotic;
    }

    public Boolean getPsychotropic() {
        return psychotropic;
    }

    public void setPsychotropic(Boolean psychotropic) {
        this.psychotropic = psychotropic;
    }

    public boolean requiresPrescription() {
        return Boolean.TRUE.equals(receipt)
                || Boolean.TRUE.equals(narcotic)
                || Boolean.TRUE.equals(psychotropic);
    }

    public String getDosageUnit() {
        return dosageUnit;
    }

    public void setDosageUnit(String dosageUnit) {
        this.dosageUnit = dosageUnit;
    }

    public StorageConditions getStorageConditions() {
        return storageConditions;
    }

    public void setStorageConditions(StorageConditions storageConditions) {
        this.storageConditions = storageConditions;
    }

    public Boolean getMarked() {
        return marked != null ? marked : false;
    }

    public void setMarked(Boolean marked) {
        this.marked = marked;
    }
}
