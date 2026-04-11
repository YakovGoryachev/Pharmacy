package DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

//todo connections

public class StockDto {
    public StockDto(){

    }

    public StockDto(Long id, Integer quantity, Integer reserved, Instant lastUpdated) {
        this.id = id;
        this.quantity = quantity;
        this.reserved = reserved;
        this.lastUpdated = lastUpdated;
    }

    private Long id;
    private Integer quantity;
    private Integer reserved;
    private Instant lastUpdated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getReserved() {
        return reserved;
    }

    public void setReserved(Integer reserved) {
        this.reserved = reserved;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
