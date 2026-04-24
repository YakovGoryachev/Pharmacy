package com.example.pharmacy.Specifications;

import com.example.pharmacy.Pojo.Batch;
import com.example.pharmacy.Pojo.Nomenclature;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class NomenclatureSpecifications {
    public static Specification<Nomenclature> hasFilters(
            String search,        // поиск по названию/МНН/штрихкоду
            String atx,           // код АТХ
            Long categoryId,      // ID категории
            String flags) {       // rx, narcotic, psycho

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 🔍 Поиск: по нескольким полям через OR
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

                // Объединяем через OR: найдёт, если совпадёт хоть в одном поле
                predicates.add(criteriaBuilder.or(byTradeName, byMnn, byBarcode, byForm));
            }

            // 🧬 Фильтр по АТХ (точное совпадение или LIKE)
            if (atx != null && !atx.isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("atxCode")),
                        atx.toUpperCase() + "%"));
            }

            // 📦 Фильтр по категории
            if (categoryId != null && categoryId > 0) {
                // Если категория — это связь @ManyToOne:
                predicates.add(criteriaBuilder.equal(
                        root.get("productCategory").get("id"), categoryId));
                // Если просто ID в таблице:
                // predicates.add(criteriaBuilder.equal(root.get("productCategoryId"), categoryId));
            }

            // 🏷️ Фильтр по флагам (рецепт/наркотик/психотроп)
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