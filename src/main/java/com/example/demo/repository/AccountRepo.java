package com.example.demo.repository;

import com.example.demo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, String> {
    @Query("SELECT new Account(a.iban) FROM Account a, Bank b, User u "+
            "WHERE b.swift = ?2 " +
            "AND u.userID = ?1 " +
            "AND a.userId = u.userID " +
            "AND a.swift = b.swift")
    Optional<Account> getAccountByUserIdAndSwift(String userId, String swift);
}
