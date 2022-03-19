package com.example.demo.repository;

import com.example.demo.model.SubAccount;
import com.example.demo.model.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.Optional;

public interface TransactionLogRepo extends JpaRepository<TransactionLog, Integer> {

    ArrayList<TransactionLog> findAllBySubAccount(SubAccount subAccount);

    @Query(value = "SELECT s FROM TransactionLog s " +
            "WHERE s.subAccount = ?1 " +
            "OR s.transactionId in " +
            "(select d.transactionId from TransactionLog d WHERE not d.subAccount = ?1 and d.direction = 0)")
    ArrayList<TransactionLog> findAllLinkedToSubAccount(SubAccount subAccount);

    /*
    @Query("SELECT t FROM TransactionLog t   WHERE ")
    Optional<TransactionLog> findTransactionLogByIban(String iban);
     */

}
