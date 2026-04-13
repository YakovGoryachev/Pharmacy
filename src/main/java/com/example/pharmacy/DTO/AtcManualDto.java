package com.example.pharmacy.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

//todo connectinos

public class AtcManualDto {
    public AtcManualDto(){

    }

    public AtcManualDto(Long id, String code, Integer level, String name, Long parentId, Instant createdAt) {
        this.id = id;
        this.code = code;
        this.level = level;
        this.name = name;
        this.parentId = parentId;
        this.createdAt = createdAt;
    }

    private Long id;
    private String code;
    private Integer level;
    private String name;
    private Long parentId;
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
