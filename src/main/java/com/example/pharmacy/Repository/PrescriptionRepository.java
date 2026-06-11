package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
}
