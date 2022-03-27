package com.example.demo.repository;

import com.example.demo.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BankRepo extends JpaRepository<Bank, String> {

    Optional<Bank> findByLogin(String login);

    @Query("SELECT " +
            "CASE WHEN COUNT(b) > 0 " +
            "THEN true " +
            "ELSE false " +
            "END " +
            "FROM Bank b " +
            "WHERE b.name = ?1")
    boolean existsByName(String name);
}
