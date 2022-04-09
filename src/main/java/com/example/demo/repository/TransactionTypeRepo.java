package com.example.demo.repository;

import com.example.demo.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * All the db request on the {@link TransactionType} table.
 */
public interface TransactionTypeRepo extends JpaRepository<TransactionType, Integer> {
}
