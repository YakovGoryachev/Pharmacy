package com.example.pharmacy.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

//todo connections

public class NomenclatureCategoryDto {
    public NomenclatureCategoryDto(){

    }

    public NomenclatureCategoryDto(Long id, String name, String code, Long parentId, boolean isSystem, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.parentId = parentId;
        this.isSystem = isSystem;
        this.createdAt = createdAt;
    }

    private Long id;
    private String name;
    private String code;
    private Long parentId;
    private boolean isSystem;
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
