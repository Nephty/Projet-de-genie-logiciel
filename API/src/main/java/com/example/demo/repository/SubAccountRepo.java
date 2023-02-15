package com.example.demo.repository;

import com.example.demo.model.Account;
import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.model.SubAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

/**
 * All the db request on the {@link SubAccount} table.
 */
public interface SubAccountRepo extends JpaRepository<SubAccount, SubAccountPK> {

    /**
     * Find all <code>SubAccount</code> from a certain account
     * @param iban The account which we want to get its SubAccounts
     * @return The SubAccounts of the Account.
     */
    @Query("select s from SubAccount s where s.iban = ?1")
    ArrayList<SubAccount> findAllByIban(Account iban);
}
