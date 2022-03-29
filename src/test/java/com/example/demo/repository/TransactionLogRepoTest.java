package com.example.demo.repository;

import com.example.demo.model.*;
import com.example.demo.model.CompositePK.SubAccountPK;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Date;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionLogRepoTest {

    @Autowired
    private TransactionLogRepo underTest;
    @Autowired
    private SubAccountRepo subAccountRepo;
    @Autowired
    private TransactionTypeRepo transactionTypeRepo;
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
        TransactionType transactionType = new TransactionType(
                0,
                "test",
                0.0
        );
        transactionTypeRepo.save(transactionType);

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
                false
        );
        accountRepo.save(account1);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void canFindAllLinkedToSubAccount(){
        // Given
        SubAccount subAccount = new SubAccount(
                accountRepo.getById("testIban"),
                currencyTypeRepo.getById(0),
                400.0
        );
        subAccountRepo.save(subAccount);

        SubAccount subAccount1 = new SubAccount(
                accountRepo.getById("test2Iban"),
                currencyTypeRepo.getById(0),
                200.0
        );
        subAccountRepo.save(subAccount1);

        TransactionLog transactionLog = new TransactionLog(
                1,
                1,
                transactionTypeRepo.getById(0),
                new Date(System.currentTimeMillis()),
                subAccount,
                100.0
        );
        underTest.save(transactionLog);

        TransactionLog transactionLog2 = new TransactionLog(
                1,
                0,
                transactionTypeRepo.getById(0),
                new Date(System.currentTimeMillis()),
                subAccount1,
                100.0
        );
        underTest.save(transactionLog2);

        TransactionLog transactionLog3 = new TransactionLog(
                2,
                1,
                transactionTypeRepo.getById(0),
                new Date(System.currentTimeMillis()),
                subAccount1,
                100.0
        );
        underTest.save(transactionLog3);

        //when
        // -- Should return transactionLog and transactionLog2 because the transaction was made by this subAccount.
        ArrayList<TransactionLog> result = underTest.findAllLinkedToSubAccount(
                subAccountRepo.getById(new SubAccountPK("testIban",0))
                );

        //Then
        assertEquals(2,result.size());
    }

    @Test
    void canFindMaxId(){
        // Given
        SubAccount subAccount = new SubAccount(
                accountRepo.getById("testIban"),
                currencyTypeRepo.getById(0),
                400.0
        );
        subAccountRepo.save(subAccount);

        SubAccount subAccount1 = new SubAccount(
                accountRepo.getById("test2Iban"),
                currencyTypeRepo.getById(0),
                200.0
        );
        subAccountRepo.save(subAccount1);

        TransactionLog transactionLog = new TransactionLog(
                1,
                1,
                transactionTypeRepo.getById(0),
                new Date(System.currentTimeMillis()),
                subAccount,
                100.0
        );
        underTest.save(transactionLog);

        TransactionLog transactionLog2 = new TransactionLog(
                1,
                0,
                transactionTypeRepo.getById(0),
                new Date(System.currentTimeMillis()),
                subAccount1,
                100.0
        );
        underTest.save(transactionLog2);

        TransactionLog transactionLog3 = new TransactionLog(
                2,
                1,
                transactionTypeRepo.getById(0),
                new Date(System.currentTimeMillis()),
                subAccount1,
                100.0
        );
        underTest.save(transactionLog3);

        //When
        Integer result = underTest.findMaximumId();

        //Then
        assertEquals(2,result);
    }
}