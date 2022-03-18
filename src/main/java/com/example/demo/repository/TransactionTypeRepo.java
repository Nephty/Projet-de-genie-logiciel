package com.example.demo.repository;

import com.example.demo.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionTypeRepo extends JpaRepository<TransactionType, Integer> {
}
