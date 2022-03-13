package com.example.demo.repository;

import com.example.demo.model.SubAccount;
import com.example.demo.model.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.Optional;

public interface TransactionLogRepo extends JpaRepository<TransactionLog, Integer> {
    Optional<TransactionLog> findAllBySubAccount(SubAccount subAccount);

    /*
    @Query("SELECT t FROM TransactionLog t   WHERE ")
    Optional<TransactionLog> findTransactionLogByIban(String iban);
     */

}
