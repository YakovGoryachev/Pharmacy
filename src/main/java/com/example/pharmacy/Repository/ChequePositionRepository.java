package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.ChequePosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ChequePositionRepository extends JpaRepository<ChequePosition, Long> {

    @Query("""
            SELECT n.brandName, SUM(p.sumOfPosition)
            FROM ChequePosition p
            JOIN p.cheque c
            JOIN p.nomenclature n
            WHERE c.isReturned = false
            AND (:pharmacyId IS NULL OR c.pharmacy.id = :pharmacyId)
            AND c.createdAt >= :from AND c.createdAt <= :to
            GROUP BY n.id, n.brandName
            ORDER BY SUM(p.sumOfPosition) DESC
            """)
    List<Object[]> topSelling(@Param("pharmacyId") Long pharmacyId,
                              @Param("from") Instant from,
                              @Param("to") Instant to);

    @Query("""
            SELECT cat.name, SUM(p.sumOfPosition)
            FROM ChequePosition p
            JOIN p.cheque c
            JOIN p.nomenclature n
            JOIN n.nomenclatureCategories cat
            WHERE c.isReturned = false
            AND (:pharmacyId IS NULL OR c.pharmacy.id = :pharmacyId)
            AND c.createdAt >= :from AND c.createdAt <= :to
            GROUP BY cat.id, cat.name
            ORDER BY SUM(p.sumOfPosition) DESC
            """)
    List<Object[]> revenueByCategory(@Param("pharmacyId") Long pharmacyId,
                                     @Param("from") Instant from,
                                     @Param("to") Instant to);

    @Query("""
            SELECT n.brandName, SUM(p.quantity)
            FROM ChequePosition p
            JOIN p.cheque c
            JOIN p.nomenclature n
            WHERE c.isReturned = false
            AND (:pharmacyId IS NULL OR c.pharmacy.id = :pharmacyId)
            AND c.createdAt >= :from AND c.createdAt <= :to
            GROUP BY n.id, n.brandName
            HAVING SUM(p.quantity) > 0
            ORDER BY SUM(p.quantity) ASC
            """)
    List<Object[]> bottomSellingByQty(@Param("pharmacyId") Long pharmacyId,
                                      @Param("from") Instant from,
                                      @Param("to") Instant to);
}
