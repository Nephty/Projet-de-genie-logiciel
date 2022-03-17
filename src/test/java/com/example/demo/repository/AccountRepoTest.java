package com.example.demo.repository;

import com.example.demo.model.Account;
import com.example.demo.model.AccountType;
import com.example.demo.model.Bank;
import com.example.demo.model.User;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccountRepoTest {

    @Autowired
    private AccountRepo underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    /*
    @Test
    void DoesNotThrowsLazyInitialisationException() {
        //Make this test on the Business layer because this error isn't thrown
        // in the persistence layer.
        //given
        Account account = new Account(
            "uwu69420",
                new Bank(),
                new User(),
                new AccountType(),
                false
        );
        //then
        underTest.getById("uwu69420");//Can throw a LazyInitialisationException
    }
     */

}