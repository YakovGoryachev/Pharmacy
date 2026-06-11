package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.Nomenclature;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NomenclatureRepository extends JpaRepository<Nomenclature, Long>, JpaSpecificationExecutor<Nomenclature> {

    @Query("""
            SELECT n FROM Nomenclature n
            WHERE LOWER(n.brandName) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(n.mnn) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(n.barcode) LIKE LOWER(CONCAT('%', :q, '%'))
            ORDER BY n.brandName
            """)
    List<Nomenclature> searchByQuery(@Param("q") String q, Pageable pageable);
}
