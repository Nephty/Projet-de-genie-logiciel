package com.example.demo.repository;

import com.example.demo.model.Account;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * All the db request on the {@link AccountAccess} table.
 */
@Repository
@Transactional
public interface AccountAccessRepo extends JpaRepository<AccountAccess, AccountAccessPK> {

    /**
     * Find all <Code>AccountAccess</Code> of the <code>User</code> ordered by Bank.
     * (only if  <code>hidden</code> is false and <code>access</code> is true).
     * This method is used to get user's portfolios
     * @see AccountAccess
     * @param user The user whose access we want to find.
     * @return All user's access
     */
    @Query("select a " +
            "from AccountAccess a " +
            "where a.userId = ?1 and a.hidden = false " +
            "AND a.accountId.deleted = false " +
            "order by a.accountId.swift.swift")
    ArrayList<AccountAccess> findAllByUserId(User user);

    @Query("select a " +
            "from AccountAccess a " +
            "where a.userId = ?1 " +
            "AND a.accountId.deleted = true " +
            "order by a.accountId.swift.swift")
    ArrayList<AccountAccess> findAllDeletedAccountByUserId(User user);

    @Query("select a " +
            "from AccountAccess a " +
            "where a.userId = ?1 and a.hidden = true " +
            "and a.access = true and a.accountId.deleted = false " +
            "order by a.accountId.swift.swift")
    ArrayList<AccountAccess> findAllHiddenByUserId(User user);

    /**
     * Check if the user has access to this account <br>
     * (Only used for test)
     * @param user The user that may have an access
     * @param account The Account that may be accessed by the user.
     * @return True if the user has access to the Account, false otherwise.
     */
    @Query("SELECT " +
            "CASE WHEN (COUNT(s.userId) > 0) " +
            "THEN true " +
            "ELSE false " +
            "END " +
            "FROM AccountAccess s " +
            "WHERE s.accountId = ?2 " +
            "AND s.userId = ?1 " +
            "AND s.access = true " +
            "AND s.accountId.deleted = false")
    boolean existsByUserIdAndAccountId(User user,Account account);


    /**
     * Find all customer of a Bank.
     * A User is considered as a customer when he has access to an account in that bank.
     * @param swift The swift of the bank.
     * @return a <code>List</code> of <code>User</code> who are customers of the bank.
     */
    @Query("SELECT s.userId " +
            "FROM AccountAccess s " +
            "WHERE s.accountId.swift.swift = ?1 and s.access = true " +
            "AND s.accountId.deleted = false")
    List<User> getAllCustomersInBank(String swift);

    /**
     * Find all Owner of the account.
     * A User is considered as an owner when he has access to the account.
     * @param account The account whose his owners we want to know.
     * @return A <code>List</code> of <code>User</code> who are owners of the account.
     */
    @Query("SELECT s.userId " +
            "FROM AccountAccess s " +
            "WHERE s.accountId = ?1 and s.access = true")
    List<User> getAllOwners(Account account);


    /**
     * Delete the Access of the requested User on the Account.
     * @param accountId The iban of the account.
     * @param userId The NRN of the user.
     */
    @Modifying
    @Query("DELETE FROM AccountAccess a " +
            "WHERE a.accountId.iban = ?1 " +
            "AND a.userId.userId = ?2")
    void deleteAccountAccessByAccountIdAndUserId(String accountId, String userId);

}

