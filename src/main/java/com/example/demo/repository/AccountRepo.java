package com.example.demo.repository;

import com.example.demo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;


@Repository
public interface AccountRepo extends JpaRepository<Account, String> {

    ArrayList<Account> findAllByNextProcessBefore(Date date);

    default ArrayList<Account> findAccountsToProcess() {
        return findAllByNextProcessBefore(new Date(System.currentTimeMillis()));
    }
}
