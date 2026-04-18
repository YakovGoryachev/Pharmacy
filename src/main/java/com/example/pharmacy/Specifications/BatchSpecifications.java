package com.example.pharmacy.Specifications;

import com.example.pharmacy.Pojo.Batch;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BatchSpecifications {
    public static Specification<Batch> hasFilters(
            String batchNumber,
            String supplier,
            LocalDate receivedDate,
            LocalDate expiryDate) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (batchNumber != null && !batchNumber.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("batchNumber"), "%" + batchNumber + "%"));
            }

            if (supplier != null && !supplier.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("supplier"), "%" + supplier + "%"));
            }

            if (receivedDate != null) {
                predicates.add(criteriaBuilder.equal(root.get("receivedDate"), receivedDate));
            }

            if (expiryDate != null) {
                predicates.add(criteriaBuilder.equal(root.get("expiryDate"), expiryDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
