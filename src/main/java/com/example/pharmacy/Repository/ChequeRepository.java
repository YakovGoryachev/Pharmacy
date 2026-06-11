package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.Cheque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ChequeRepository extends JpaRepository<Cheque, Long> {

    Optional<Cheque> findByNumberCheque(String numberCheque);

    @Query("""
            SELECT c FROM Cheque c
            WHERE c.isReturned = false
            AND (:pharmacyId IS NULL OR c.pharmacy.id = :pharmacyId)
            AND c.createdAt BETWEEN :from AND :to
            """)
    List<Cheque> findSalesInPeriod(@Param("pharmacyId") Long pharmacyId,
                                   @Param("from") Instant from,
                                   @Param("to") Instant to);

    List<Cheque> findByPharmacyIdOrderByCreatedAtDesc(Long pharmacyId);
}
