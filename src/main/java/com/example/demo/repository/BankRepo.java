package com.example.demo.repository;

import com.example.demo.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepo extends JpaRepository<Bank, String> {
    Optional<Bank> findByLogin(String login);
}
