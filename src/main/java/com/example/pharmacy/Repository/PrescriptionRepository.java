package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
            FROM Prescription p
            JOIN p.chequePosition pos
            JOIN pos.cheque ch
            WHERE LOWER(TRIM(p.prescriptionNumber)) = LOWER(TRIM(:number))
            AND COALESCE(ch.isReturned, false) = false
            """)
    boolean existsInActiveSale(@Param("number") String prescriptionNumber);
}
