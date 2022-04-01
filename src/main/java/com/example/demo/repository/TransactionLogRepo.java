package com.example.demo.repository;

import com.example.demo.model.SubAccount;
import com.example.demo.model.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Optional;

public interface TransactionLogRepo extends JpaRepository<TransactionLog, Integer> {

    @Query(value = "SELECT s FROM TransactionLog s WHERE s.transactionId IN (SELECT d.transactionId FROM TransactionLog d WHERE d.subAccount= ?1)")
    ArrayList<TransactionLog> findAllLinkedToSubAccount(SubAccount subAccount);

    @Query("SELECT max(s.transactionId) from TransactionLog s")
    Integer findMaximumId();

    ArrayList<TransactionLog> findAllByProcessedOrderByTransactionId(Boolean processed);

    void deleteAllByTransactionId(Integer transactionId);

    ArrayList<TransactionLog> findAllByTransaction_dateBeforeAndProcessedOrderByTransactionId(Date now, Boolean processed);
}
