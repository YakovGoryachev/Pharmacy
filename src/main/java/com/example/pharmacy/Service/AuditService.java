package com.example.pharmacy.Service;

import com.example.pharmacy.Pojo.AuditLog;
import com.example.pharmacy.Pojo.User;
import com.example.pharmacy.Repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    public void log(String action, String entityName, Long entityId, Object oldVal, Object newVal) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setOldValues(toJson(oldVal));
        log.setNewValues(toJson(newVal));
        log.setUser(currentUser());
        auditLogRepository.save(log);
    }

    public Page<AuditLog> findAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    private String toJson(Object o) {
        if (o == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            return "\"" + o.toString() + "\"";
        }
    }

    private User currentUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof com.example.pharmacy.security.PharmaUserDetails details) {
                return details.getUser();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
