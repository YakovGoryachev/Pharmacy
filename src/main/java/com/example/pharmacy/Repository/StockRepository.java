package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByPharmacyIdAndBatchId(Long pharmacyId, Long batchId);

    List<Stock> findByPharmacyId(Long pharmacyId);

    @Query("""
            SELECT s FROM Stock s
            JOIN FETCH s.batch b
            JOIN FETCH b.nomenclature n
            WHERE s.pharmacy.id = :pharmacyId
            AND (:nomenclatureId IS NULL OR n.id = :nomenclatureId)
            """)
    List<Stock> findWithBatchAndNomenclature(@Param("pharmacyId") Long pharmacyId,
                                             @Param("nomenclatureId") Long nomenclatureId);

    @Query("""
            SELECT s FROM Stock s
            JOIN FETCH s.batch b
            JOIN FETCH b.nomenclature n
            JOIN FETCH s.pharmacy p
            WHERE n.id = :nomenclatureId AND s.quantity > s.reserved
            ORDER BY b.expiryDate ASC
            """)
    List<Stock> findAvailableByNomenclatureFefo(@Param("nomenclatureId") Long nomenclatureId);

    @Query("""
            SELECT s FROM Stock s
            JOIN FETCH s.batch b
            JOIN FETCH b.nomenclature n
            WHERE s.pharmacy.id = :pharmacyId
            AND b.expiryDate <= :before
            AND s.quantity > 0
            ORDER BY b.expiryDate ASC
            """)
    List<Stock> findExpiring(@Param("pharmacyId") Long pharmacyId, @Param("before") LocalDate before);

    @Query("""
            SELECT s FROM Stock s
            JOIN FETCH s.batch b
            JOIN FETCH b.nomenclature n
            JOIN FETCH s.pharmacy p
            WHERE b.expiryDate <= :before
            AND s.quantity > 0
            ORDER BY b.expiryDate ASC, p.name ASC
            """)
    List<Stock> findExpiringNetwork(@Param("before") LocalDate before);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.batch.id = :batchId")
    int sumQuantityByBatchId(@Param("batchId") Long batchId);

    @Query("""
            SELECT COALESCE(SUM(s.quantity - s.reserved), 0) FROM Stock s
            JOIN s.batch b
            WHERE s.pharmacy.id = :pharmacyId
            AND b.nomenclature.id = :nomenclatureId
            AND (b.writtenOff IS NULL OR b.writtenOff = false)
            AND b.expiryDate >= :today
            AND s.quantity > s.reserved
            """)
    int sumAvailableForSale(@Param("pharmacyId") Long pharmacyId,
                            @Param("nomenclatureId") Long nomenclatureId,
                            @Param("today") LocalDate today);

    @Query("""
            SELECT s FROM Stock s
            JOIN FETCH s.batch b
            JOIN FETCH b.nomenclature n
            JOIN FETCH s.pharmacy p
            WHERE s.quantity > s.reserved
            AND (b.writtenOff IS NULL OR b.writtenOff = false)
            AND b.expiryDate >= :today
            AND (:pharmacyId IS NULL OR p.id = :pharmacyId)
            """)
    List<Stock> findAvailableForCatalog(@Param("pharmacyId") Long pharmacyId,
                                        @Param("today") LocalDate today);
}