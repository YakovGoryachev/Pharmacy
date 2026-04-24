package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.NomenclatureCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<NomenclatureCategory,Long> {
    List<NomenclatureCategory> findByNameContainingIgnoreCase(String name);
    Optional<NomenclatureCategory> findByNameIgnoreCase(String name);
}
