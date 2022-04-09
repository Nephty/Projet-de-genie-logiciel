package com.example.demo.repository;

import com.example.demo.model.CompositePK.TransactionLogPK;
import com.example.demo.model.SubAccount;
import com.example.demo.model.TransactionLog;
import com.example.demo.service.TransactionLogService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * All the db request on the {@link TransactionLog} table.
 */
public interface TransactionLogRepo extends JpaRepository<TransactionLog, TransactionLogPK> {

    /**
     * Find all transactions for the subAccount (received and sent)
     * @param subAccount The SubAccount whose his transaction we want to find.
     * @return All the transactions of the subAccount.
     */
    @Query("SELECT s " +
            "FROM TransactionLog s " +
            "WHERE s.transactionId " +
            "IN (SELECT d.transactionId FROM TransactionLog d WHERE d.subAccount= ?1)")
    ArrayList<TransactionLog> findAllLinkedToSubAccount(SubAccount subAccount);

    /**
     * Use to get the next id for a new transaction.
     * We use this technique for the id's because we instantiate 2 transactions with the same id
     * for both direction (in and out).
     * @see TransactionLogService
     * @return The maximum ID of the transaction stored in the DB
     */
    @Query("SELECT max(s.transactionId) from TransactionLog s")
    Integer findMaximumId();

    /**
     * (only used in {@link #findAllToExecute()}
     * @param date The date to use in the request.
     * @param processed If we want to processed transactions or not
     * @return All the transaction made before the date, processed (or not)
     */
    @Query("select t " +
            "from TransactionLog t " +
            "where t.transactionDate <= ?1 " +
            "and t.processed = ?2 " +
            "order by t.transactionId")
    ArrayList<TransactionLog> findAllByTransactionDateBeforeAndProcessedOrderByTransactionId(
            Date date,
            Boolean processed
    );

    /**
     * Find all the single transactions (only in or only out)
     * Used for the scheduler to check if there's a problem in the db.
     * @return ArrayList of transactions that are badly formatted.
     */
    @Query("SELECT t from TransactionLog t " +
            "WHERE not exists " +
            "(SELECT b FROM TransactionLog b " +
            "WHERE t.transactionId = b.transactionId " +
            "and not t.isSender = b.isSender)")
    ArrayList<TransactionLog> findBadFormatTransaction();

    /**
     * Find all transaction to execute (ie not processed and older than today)
     * @return {@link #findAllByTransactionDateBeforeAndProcessedOrderByTransactionId(Date, Boolean)}
     */
    default ArrayList<TransactionLog> findAllToExecute() {
        return findAllByTransactionDateBeforeAndProcessedOrderByTransactionId(
                Date.valueOf(LocalDate.now()),
                false
        );
    }

    /**
     * delete all the transaction with the transactionId.
     * We can't use <code>deleteById(Integer)</code> given by JPA because it only deletes one transaction.
     * In our case, we have 2 lines in the DB for each transaction (in and out).
     * @param transactionId the id of the transaction we want to delete.
     */
    void deleteAllByTransactionId(Integer transactionId);
}
