package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.Nomenclature;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NomenclatureRepository extends JpaRepository<Nomenclature, Long>, JpaSpecificationExecutor<Nomenclature> {

}
