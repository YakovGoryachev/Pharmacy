package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.RequestReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestReportRepository extends JpaRepository<RequestReport, Long> {
    List<RequestReport> findByUserIdOrderByGeneratedAtDesc(Long userId);
}
