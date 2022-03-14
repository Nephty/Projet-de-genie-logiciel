package com.example.demo.repository;

import com.example.demo.model.Account;
import com.example.demo.model.AccountType;
import com.example.demo.model.Bank;
import com.example.demo.model.User;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccountRepoTest {

    @Autowired
    private AccountRepo underTest;


    @Test
    void DoesNotThrowsLazyInitialisationException() {
        //given
        Account account = new Account(
            "uwu69420",
                new Bank(),
                new User(),
                new AccountType(),
                false
        );
        //then
        assertThrows(LazyInitializationException.class,()-> underTest.getById("uwu69420"));
    }
}