package com.example.pharmacy.DTO;

import com.example.pharmacy.Pojo.Address;

import java.time.Instant;

//todo connectinos

public class PharmacyDto {
    public PharmacyDto(){

    }

    public PharmacyDto(Long id, String name, Address address, Integer numberLicense, String phoneNumber, String email, boolean is_active, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.numberLicense = numberLicense;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.is_active = is_active;
        this.createdAt = createdAt;
    }

    private Long id;
    private String name;
    private Address address;
    private Integer numberLicense;
    private String phoneNumber;
    private String email;
    private boolean is_active;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Integer getNumberLicense() {
        return numberLicense;
    }

    public void setNumberLicense(Integer numberLicense) {
        this.numberLicense = numberLicense;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
