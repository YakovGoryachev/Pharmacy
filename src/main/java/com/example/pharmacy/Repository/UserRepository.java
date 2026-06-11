package com.example.pharmacy.Repository;

import com.example.pharmacy.Pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role LEFT JOIN FETCH u.pharmacy WHERE u.login = :login")
    Optional<User> findByLoginWithDetails(@Param("login") String login);

    boolean existsByLogin(String login);

    long countByPharmacyId(Long pharmacyId);
}
