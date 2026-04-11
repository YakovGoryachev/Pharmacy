package DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;

//todo connectinos, JSON(record)

import java.time.Instant;

public class ChequePositionDto {
    public ChequePositionDto(){

    }

    public ChequePositionDto(Long id, Integer quantity, Integer cost, Integer sumOfPosition, Instant createdAt) {
        this.id = id;
        this.quantity = quantity;
        this.cost = cost;
        this.sumOfPosition = sumOfPosition;
        this.createdAt = createdAt;
    }

    private Long id;
    private Integer quantity;
    private Integer cost;
    private Integer sumOfPosition;
    private record receiptData(

    ){}
    private Instant createdAt;

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

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getSumOfPosition() {
        return sumOfPosition;
    }

    public void setSumOfPosition(Integer sumOfPosition) {
        this.sumOfPosition = sumOfPosition;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
