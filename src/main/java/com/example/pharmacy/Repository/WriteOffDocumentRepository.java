package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.WriteOffDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface WriteOffDocumentRepository extends JpaRepository<WriteOffDocument, Long> {

    @Query("""
            SELECT COALESCE(SUM(w.quantity), 0)
            FROM WriteOffDocument w
            WHERE (:pharmacyId IS NULL OR w.pharmacy.id = :pharmacyId)
            AND w.createdAt >= :from AND w.createdAt <= :to
            """)
    Integer sumQuantityInPeriod(@Param("pharmacyId") Long pharmacyId,
                                @Param("from") Instant from,
                                @Param("to") Instant to);
}
