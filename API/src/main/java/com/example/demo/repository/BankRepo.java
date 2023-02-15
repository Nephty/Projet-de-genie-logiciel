package com.example.demo.repository;

import com.example.demo.model.AccountType;
import com.example.demo.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * All the db request on the {@link AccountType} table.
 */
public interface BankRepo extends JpaRepository<Bank, String> {

    /**
     * Check if the bank name already exist
     * @param name The name we want to know if it already exists
     * @return true if the name is already used for a bank, false otherwise.
     */
    @Query("SELECT " +
            "CASE WHEN COUNT(b) > 0 " +
            "THEN true " +
            "ELSE false " +
            "END " +
            "FROM Bank b " +
            "WHERE b.name = ?1")
    boolean existsByName(String name);
}
