package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByEntityNameContainingIgnoreCase(String entityName, Pageable pageable);

    Page<AuditLog> findByTimestampBetween(Instant from, Instant to, Pageable pageable);
}
