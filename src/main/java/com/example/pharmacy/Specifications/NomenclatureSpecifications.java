package com.example.pharmacy.Specifications;

import com.example.pharmacy.Pojo.Batch;
import com.example.pharmacy.Pojo.Nomenclature;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class NomenclatureSpecifications {
    public static Specification<Nomenclature> hasFilters(
            String search,        
            String atx,           
            Long categoryId,      
            String flags) {       

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (search != null && !search.isBlank()) {
                String likePattern = "%" + search.toLowerCase() + "%";

                Predicate byTradeName = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("brandName")), likePattern);
                Predicate byMnn = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("mnn")), likePattern);
                Predicate byBarcode = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("barcode")), likePattern);
                Predicate byForm = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("formOfRelease")), likePattern);

                predicates.add(criteriaBuilder.or(byTradeName, byMnn, byBarcode, byForm));
            }

            if (atx != null && !atx.isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.upper(
                                root.join("atcManual", JoinType.LEFT).get("code")
                        ),
                        atx.toUpperCase() + "%"));
            }

//            if (categoryId != null && categoryId > 0) {
//                predicates.add(criteriaBuilder.equal(
//                        root.get("productCategory").get("id"), categoryId));
//                // predicates.add(criteriaBuilder.equal(root.get("productCategoryId"), categoryId));
//            }
            if (categoryId != null && categoryId > 0) {
                predicates.add(criteriaBuilder.equal(
                        root.join("nomenclatureCategories", JoinType.INNER).get("id"),
                        categoryId));
            }

            if (flags != null && !flags.isBlank()) {
                switch (flags) {
                    case "rx" -> predicates.add(criteriaBuilder.isTrue(root.get("receipt")));
                    case "narcotic" -> predicates.add(criteriaBuilder.isTrue(root.get("narcotic")));
                    case "psycho" -> predicates.add(criteriaBuilder.isTrue(root.get("psychotropic")));
                }
            }

            return predicates.isEmpty() ? null : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}