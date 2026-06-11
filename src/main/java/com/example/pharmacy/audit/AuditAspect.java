package com.example.pharmacy.audit;

import com.example.pharmacy.Service.AuditService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @AfterReturning(pointcut = "@annotation(audited)", returning = "result")
    public void after(JoinPoint jp, Audited audited, Object result) {
        Long id = extractId(result);
        auditService.log(audited.action(), audited.entity(), id, null, result);
    }

    private Long extractId(Object result) {
        if (result == null) {
            return null;
        }
        try {
            var m = result.getClass().getMethod("getId");
            Object id = m.invoke(result);
            if (id instanceof Long l) {
                return l;
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
