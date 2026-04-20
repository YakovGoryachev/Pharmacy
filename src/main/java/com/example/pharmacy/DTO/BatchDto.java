package com.example.pharmacy.DTO;

//todo connections

import java.time.LocalDate;
import java.util.Date;

public class BatchDto {
    public BatchDto(){

    }

    public BatchDto(Long id, Long nomenclatureId, LocalDate productionDate, String batchNumber, LocalDate expiryDate, LocalDate receivedDate, String supplier, Integer price, Integer qtyReceived, Integer qtyInStock, String storageZone) {
        this.id = id;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.receivedDate = receivedDate;
        this.supplier = supplier;
        this.price = price;
        this.qtyReceived = qtyReceived;
        this.qtyInStock = qtyInStock;
        this.storageZone = storageZone;
    }

    private Long id;
    private Long nomenclatureId;
    private String batchNumber;
    private LocalDate expiryDate;
    private LocalDate productionDate;
    private LocalDate receivedDate;
    private String supplier;
    private Integer price;
    private Integer qtyReceived;
    private Integer qtyInStock;
    private String storageZone;
    private Boolean isWrittenOff;

    public Boolean isWrittenOff() {
        return isWrittenOff;
    }

    public void setWrittenOff(Boolean writtenOff) {
        isWrittenOff = writtenOff;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getQtyReceived() {
        return qtyReceived;
    }

    public void setQtyReceived(Integer qtyReceived) {
        this.qtyReceived = qtyReceived;
    }

    public Integer getQtyInStock() {
        return qtyInStock;
    }

    public void setQtyInStock(Integer qtyInStock) {
        this.qtyInStock = qtyInStock;
    }

    public String getStorageZone() {
        return storageZone;
    }

    public void setStorageZone(String storageZone) {
        this.storageZone = storageZone;
    }

    public Long getNomenclatureId() {
        return nomenclatureId;
    }

    public void setNomenclatureId(Long nomenclatureId) {
        this.nomenclatureId = nomenclatureId;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }
}
