package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.MarkingCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarkingCodeRepository extends JpaRepository<MarkingCode, Long> {
    Optional<MarkingCode> findByCode(String code);
    boolean existsByCode(String code);
    List<MarkingCode> findByMdlpStatus(String mdlpStatus);
    List<MarkingCode> findByBatchId(Long batchId);
}
