package com.example.pharmacy.DTO;

public class CashierProductRowDto {

    private Long nomenclatureId;
    private String mnn;
    private String brandName;
    private String dosageText;
    private Integer price;
    private Integer availableQty;
    private Long pharmacyId;
    private String pharmacyName;
    private Boolean receipt;
    private Boolean marked;
    private boolean canAddToCart;

    public Long getNomenclatureId() {
        return nomenclatureId;
    }

    public void setNomenclatureId(Long nomenclatureId) {
        this.nomenclatureId = nomenclatureId;
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

    public String getDosageText() {
        return dosageText;
    }

    public void setDosageText(String dosageText) {
        this.dosageText = dosageText;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Integer availableQty) {
        this.availableQty = availableQty;
    }

    public Long getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(Long pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public Boolean getReceipt() {
        return receipt;
    }

    public void setReceipt(Boolean receipt) {
        this.receipt = receipt;
    }

    public Boolean getMarked() {
        return marked;
    }

    public void setMarked(Boolean marked) {
        this.marked = marked;
    }

    public boolean isCanAddToCart() {
        return canAddToCart;
    }

    public void setCanAddToCart(boolean canAddToCart) {
        this.canAddToCart = canAddToCart;
    }
}
