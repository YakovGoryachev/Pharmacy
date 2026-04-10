package Pojo;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.management.InstanceNotFoundException;
import java.time.Instant;

//todo make constraints

@Entity
@Table(name="pharmacies")
public class Pharmacy {
    public Pharmacy(){

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Embedded
    private Address address;
    private Integer numberLicense;
    private String phoneNumber;
    private String email;
    private boolean is_active;
    @CreationTimestamp
    private Instant createdAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
