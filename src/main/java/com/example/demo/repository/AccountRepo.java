package com.example.demo.repository;

import com.example.demo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, String> {
    @Query("SELECT new Account(a.iban,a.swift) FROM Account a, Bank b, User u "+
            "WHERE u.userID = ?1 " +
            "AND a.userId = u.userID " +
            "AND a.swift = b.swift " +
            "group by b")
    Optional<Account> getAccountByUserId(String userId);


    Optional<Account> getAccountByIban(String iban);
}
