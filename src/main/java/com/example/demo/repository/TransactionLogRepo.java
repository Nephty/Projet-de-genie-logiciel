package com.example.demo.repository;

import com.example.demo.model.CompositePK.TransactionLogPK;
import com.example.demo.model.SubAccount;
import com.example.demo.model.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TemporalType;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public interface TransactionLogRepo extends JpaRepository<TransactionLog, TransactionLogPK> {

    @Query(value = "SELECT s FROM TransactionLog s WHERE s.transactionId IN (SELECT d.transactionId FROM TransactionLog d WHERE d.subAccount= ?1)")
    ArrayList<TransactionLog> findAllLinkedToSubAccount(SubAccount subAccount);

    @Query("SELECT max(s.transactionId) from TransactionLog s")
    Integer findMaximumId();

    @Query("select t " +
            "from TransactionLog t " +
            "where t.processed = ?1 " +
            "order by t.transactionId")
    ArrayList<TransactionLog> findAllByProcessedOrderByTransactionId(Boolean processed);

    @Query("select t " +
            "from TransactionLog t " +
            "where t.transactionDate <= ?1 " +
            "and t.processed = ?2 " +
            "order by t.transactionId")
    ArrayList<TransactionLog> findAllByTransactionDateBeforeAndProcessedOrderByTransactionId(
            Date now,
            Boolean processed
    );

    default ArrayList<TransactionLog> findAllToExecute() {
        return findAllByTransactionDateBeforeAndProcessedOrderByTransactionId(
                Date.valueOf(LocalDate.now()),
                false
        );
    }

    void deleteAllByTransactionId(Integer transactionId);
}
