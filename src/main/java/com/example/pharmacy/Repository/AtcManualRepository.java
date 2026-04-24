package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.AtcManual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AtcManualRepository extends JpaRepository<AtcManual, Long> {

    @Query("SELECT a FROM AtcManual a WHERE " +
            "UPPER(a.code) LIKE UPPER(CONCAT(:query, '%')) OR " +
            "LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<AtcManual> searchByCodeOrName(
            @Param("query") String query
    );
    AtcManual findByName(String name);
    AtcManual findByCode(String code);
}
