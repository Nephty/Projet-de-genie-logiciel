package com.example.demo.repository;

import com.example.demo.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class AccountRepoTest {

    @Autowired
    private AccountRepo underTest;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private BankRepo bankRepo;
    @Autowired
    private CurrencyTypeRepo currencyTypeRepo;
    @Autowired
    private AccountTypeRepo accountTypeRepo;


    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @BeforeEach
    void setUp() {
        User user = new User(
                "testId",
                "imATest",
                "lastName",
                "firstName",
                "test@email.com",
                "passwordTested",
                "EN"
        );
        userRepo.save(user);

        CurrencyType currencyType = new CurrencyType(0, "EUR");
        currencyTypeRepo.save(currencyType);

        Bank bank = new Bank(
                "testSwift1",
                "test",
                "password",
                "address",
                "UK",
                currencyType
        );
        bankRepo.save(bank);

        AccountType accountType = new AccountType(
                0,
                "test",
                0.1,
                10.2,
                null,
                null
        );
        accountTypeRepo.save(accountType);
    }

    @Test
    void findAllByNextProcessBefore() {
        // Given
        Account account = new Account(
                "testIban",
                bankRepo.getById("testSwift1"),
                userRepo.getById("testId"),
                accountTypeRepo.getById(0),
                false,
                Date.valueOf(LocalDate.of(2002,10,31)),
                false
        );
        accountRepo.save(account);

        Account account2 = new Account(
                "testIban2",
                bankRepo.getById("testSwift1"),
                userRepo.getById("testId"),
                accountTypeRepo.getById(0),
                false,
                Date.valueOf(LocalDate.of(4000,10,12)),
                false
        );
        accountRepo.save(account2);

        Account account3 = new Account(
                "testIban3",
                bankRepo.getById("testSwift1"),
                userRepo.getById("testId"),
                accountTypeRepo.getById(0),
                false,
                Date.valueOf(LocalDate.of(2003,10,31)),
                true
        );
        accountRepo.save(account3);


        // When
        List<Account> result = underTest.findAllByNextProcessBefore(Date.valueOf(LocalDate.now()));

        // Then
        assertEquals(1,result.size());
        assertEquals(account.getIban(),result.get(0).getIban());
    }
}