package com.example.demo.repository;

import com.example.demo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Optional;

/**
 * All the db request on the {@link Account} table.
 */
@Repository
public interface AccountRepo extends JpaRepository<Account, String> {

    /**
     * (Only used in {@link #findAccountsToProcess()}
     * @param date The date to use in the request.
     * @return {@link ArrayList} of the {@link Account} that must pay a fee or receive an annual return
     */
    @Query("select a " +
            "from Account a " +
            "where a.nextProcess < ?1 " +
            "and a.deleted = false")
    ArrayList<Account> findAllByNextProcessBefore(Date date);

    /**
     * Find all the account that must pay a fee or receive an annual return.
     * (ie the next process is before the actual Date)
     * @return {@link #findAllByNextProcessBefore(Date)} with the date of the day.
     */
    default ArrayList<Account> findAccountsToProcess() {
        return findAllByNextProcessBefore(new Date(System.currentTimeMillis()));
    }

    /**
     * Find by id but return an empty optional if the account is deleted.
     * @param iban The iban of the account
     * @return An Optional of account. Empty if the account doesn't exist or is deleted.
     * @see #findById(Object) 
     */
    default Optional<Account> safeFindById(String iban) {
        return findById(iban).map(account -> account.getDeleted() ? null : account);
    }
}
