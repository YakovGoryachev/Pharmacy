package com.example.pharmacy.DTO;

import java.io.Serializable;

public class CartItemDto implements Serializable {
    private Long nomenclatureId;
    private Long batchId;
    private Long stockId;
    private String displayName;
    private int quantity;
    private int price;
    private boolean receiptRequired;
    private boolean marked;
    private String markingCode;
    private PrescriptionFormDto prescription;

    public Long getNomenclatureId() {
        return nomenclatureId;
    }

    public void setNomenclatureId(Long nomenclatureId) {
        this.nomenclatureId = nomenclatureId;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isReceiptRequired() {
        return receiptRequired;
    }

    public void setReceiptRequired(boolean receiptRequired) {
        this.receiptRequired = receiptRequired;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public String getMarkingCode() {
        return markingCode;
    }

    public void setMarkingCode(String markingCode) {
        this.markingCode = markingCode;
    }

    public PrescriptionFormDto getPrescription() {
        return prescription;
    }

    public void setPrescription(PrescriptionFormDto prescription) {
        this.prescription = prescription;
    }

    public int getLineTotal() {
        return price * quantity;
    }
}
