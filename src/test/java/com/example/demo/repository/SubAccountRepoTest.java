package com.example.demo.repository;

import com.example.demo.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class SubAccountRepoTest {

    @Autowired
    private SubAccountRepo underTest;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private BankRepo bankRepo;
    @Autowired
    private CurrencyTypeRepo currencyTypeRepo;
    @Autowired
    private AccountTypeRepo accountTypeRepo;
    @Autowired
    private UserRepo userRepo;

    @BeforeEach
    void setUp() {

        CurrencyType currencyType = new CurrencyType(0,"EUR");
        currencyTypeRepo.save(currencyType);

        Bank bank = new Bank(
                "testSwift",
                "test",
                "password",
                "address",
                "English",
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

        Account account = new Account(
                "testIban",
                bank,
                user,
                accountType,
                false,
                Date.valueOf(LocalDate.now()),
                false
        );
        accountRepo.save(account);


        User user1 = new User(
                "test2Id",
                "imATest2",
                "lastName",
                "FirstName",
                "test5@gmail.com",
                "passwordTested",
                "En"
        );
        userRepo.save(user1);

        Account account1 = new Account(
                "test2Iban",
                bank,
                user1,
                accountType,
                false,
                Date.valueOf(LocalDate.now()),
                false
        );
        accountRepo.save(account1);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findAllByIban() {
        // Given
        SubAccount subAccount = new SubAccount(
                accountRepo.getById("testIban"),
                currencyTypeRepo.getById(0),
                400.0
        );
        underTest.save(subAccount);

        SubAccount subAccount1 = new SubAccount(
                accountRepo.getById("test2Iban"),
                currencyTypeRepo.getById(0),
                200.0
        );
        underTest.save(subAccount1);

        // When
        ArrayList<SubAccount> result = underTest.findAllByIban(accountRepo.getById("testIban"));

        // Then
        assertEquals(1,result.size());
        assertEquals(subAccount.getIban().getIban(),result.get(0).getIban().getIban());
        assertEquals(subAccount.getCurrencyType(),result.get(0).getCurrencyType());
    }
}