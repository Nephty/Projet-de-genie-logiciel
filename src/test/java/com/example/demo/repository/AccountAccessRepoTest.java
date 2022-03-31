package com.example.demo.repository;

import com.example.demo.model.*;
import com.example.demo.model.CompositePK.AccountAccessPK;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccountAccessRepoTest {

    @Autowired
    private AccountAccessRepo underTest;
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

        Account account = new Account(
                "testIban",
                bank,
                user,
                accountType,
                false
        );
        accountRepo.save(account);


    }

    @Test
    void canGetAllByUserId() {
        //given

        AccountAccess testedWithTestId = new AccountAccess(
                accountRepo.getById("testIban"),
                userRepo.getById("testId"),
                true,
                false
        );
        underTest.save(testedWithTestId);

        AccountAccess testedWithoutTestId = new AccountAccess(
                accountRepo.getById("testIban"),
                userRepo.getById("test2Id"),
                true,
                false
        );
        underTest.save(testedWithoutTestId);

        // when
        List<AccountAccess> result = underTest.getAllByUserId("testId");
        //then
        assertEquals(1,result.size());
        assertEquals("testId", result.get(0).getUserId().getUserID());
    }

    @Test
    void existsByUserIdAndAccountId(){
        //Given
        AccountAccess accessInDb = new AccountAccess(
                accountRepo.getById("testIban"),
                userRepo.getById("testId"),
                true,
                false
        );
        underTest.save(accessInDb);

        //When
        boolean shouldBeTrue = underTest.existsByUserIdAndAccountId(
                accessInDb.getUserId(),
                accessInDb.getAccountId()
        );

        boolean shouldBeFalse = underTest.existsByUserIdAndAccountId(
                userRepo.getById("test2Id"),
                accessInDb.getAccountId()
        );

        //Then
        assertTrue(shouldBeTrue);
        assertFalse(shouldBeFalse);

    }

    @Test
    void canGetAllCustomersInBank(){
        //Given
        String swift = "testSwift";

        AccountAccess testedWithTestId = new AccountAccess(
                accountRepo.getById("testIban"),
                userRepo.getById("testId"),
                true,
                false
        );
        underTest.save(testedWithTestId);

        //When
        List<User> result = underTest.getAllCustomersInBank(swift);

        //then
        // -- Should only return the user that has access to an account of the bank "testSwift"
        assertEquals(1,result.size());
        assertEquals("testId",result.get(0).getUserID());
    }

    @Test
    void canDeleteAccountAccessByAccountIdAndUserId() {

        AccountAccess testedWithTestId = new AccountAccess(
                accountRepo.getById("testIban"),
                userRepo.getById("testId"),
                true,
                false
        );
        underTest.save(testedWithTestId);

        AccountAccess testedWithoutTestId = new AccountAccess(
                accountRepo.getById("testIban"),
                userRepo.getById("test2Id"),
                true,
                false
        );
        underTest.save(testedWithoutTestId);

        //given
        String accountId = "testIban";
        String userId = "testId";
        String persistUserid = "test2Id";
        //when
        underTest.deleteAccountAccessByAccountIdAndUserId(accountId,userId);

        //then
        assertFalse(underTest.existsByUserIdAndAccountId(userRepo.getById(userId),accountRepo.getById(accountId)));
        assertTrue(underTest.existsByUserIdAndAccountId(userRepo.getById(persistUserid),accountRepo.getById(accountId)));
    }
}