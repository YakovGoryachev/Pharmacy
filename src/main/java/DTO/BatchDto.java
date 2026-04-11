package DTO;

//todo connections

import java.util.Date;

public class BatchDto {
    public BatchDto(){

    }

    public BatchDto(Long id, String batchNumber, Date expiryDate, Date receivedDate, String supplier, Integer price, Integer qtyReceived, Integer qtyInStock, String storageZone) {
        this.id = id;
        BatchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.receivedDate = receivedDate;
        this.supplier = supplier;
        this.price = price;
        this.qtyReceived = qtyReceived;
        this.qtyInStock = qtyInStock;
        this.storageZone = storageZone;
    }

    private Long id;
    private String BatchNumber;
    private Date expiryDate;
    private Date receivedDate;
    private String supplier;
    private Integer price;
    private Integer qtyReceived;
    private Integer qtyInStock;
    private String storageZone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchNumber() {
        return BatchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        BatchNumber = batchNumber;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
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
}
