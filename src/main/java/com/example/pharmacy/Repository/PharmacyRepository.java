package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
    List<Pharmacy> findByActiveTrue();
}
