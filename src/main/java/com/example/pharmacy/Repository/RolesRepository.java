package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByName(String name);

    List<Roles> findByNameInOrderByDescriptionAsc(Collection<String> names);
}
