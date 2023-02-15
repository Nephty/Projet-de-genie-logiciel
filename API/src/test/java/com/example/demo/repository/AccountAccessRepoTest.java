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
                "EN",
                Date.valueOf(LocalDate.of(2002,10,31))
        );
        userRepo.save(user);

        User user1 = new User(
                "test2Id",
                "imATest2",
                "lastName",
                "FirstName",
                "test5@gmail.com",
                "passwordTested",
                "En",
                Date.valueOf(LocalDate.of(2002,10,31))
        );
        userRepo.save(user1);

        CurrencyType currencyType = new CurrencyType(0,"EUR");
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

        Bank bank2 = new Bank(
                "testSwift2",
                "test2",
                "password",
                "address",
                "UK",
                currencyType
        );
        bankRepo.save(bank2);

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
                false,
                new Date(System.currentTimeMillis()),
                false
        );
        accountRepo.save(account);

        Account account2 = new Account(
                "testIban2",
                bank2,
                user,
                accountType,
                false,
                Date.valueOf(LocalDate.now()),
                false
        );
        accountRepo.save(account2);

        Account account3 = new Account(
                "testIban3",
                bank,
                user,
                accountType,
                false,
                Date.valueOf(LocalDate.now()),
                false
        );
        accountRepo.save(account3);

        Account account4 = new Account(
                "deletedAccount",
                bank,
                user,
                accountType,
                false,
                Date.valueOf(LocalDate.now()),
                true
        );
        accountRepo.save(account4);


    }

    @Test
    void canFindAllByUserId() {
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

        AccountAccess testedWithHiddenAccess = new AccountAccess(
                accountRepo.getById("testIban2"),
                userRepo.getById("testId"),
                true,
                true
        );
        underTest.save(testedWithHiddenAccess);

        AccountAccess testedWithDeletedAccount = new AccountAccess(
                accountRepo.getById("deletedAccount"),
                userRepo.getById("testId"),
                true,
                false
        );
        underTest.save(testedWithDeletedAccount);

        // when
        ArrayList<AccountAccess> result = underTest.findAllByUserId(testedWithTestId.getUserId());
        //then
        assertEquals(1,result.size());
        assertEquals("testId", result.get(0).getUserId().getUserId());
    }

    @Test
    void canFindAllDeletedByUserId(){
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

        AccountAccess testedWithHiddenAccess = new AccountAccess(
                accountRepo.getById("testIban2"),
                userRepo.getById("testId"),
                true,
                true
        );
        underTest.save(testedWithHiddenAccess);

        AccountAccess testedWithDeletedAccount = new AccountAccess(
                accountRepo.getById("deletedAccount"),
                userRepo.getById("testId"),
                true,
                false
        );
        underTest.save(testedWithDeletedAccount);

        // when
        ArrayList<AccountAccess> result = underTest.findAllDeletedAccountByUserId(testedWithTestId.getUserId());
        //then
        assertEquals(1,result.size());
        assertEquals("testId", result.get(0).getUserId().getUserId());
        assertEquals("deletedAccount",result.get(0).getAccountId().getIban());
    }

    @Test
    void canFindAllHiddenByUserId(){
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

        AccountAccess testedWithHiddenAccess = new AccountAccess(
                accountRepo.getById("testIban2"),
                userRepo.getById("testId"),
                true,
                true
        );
        underTest.save(testedWithHiddenAccess);

        AccountAccess testedWithDeletedAccount = new AccountAccess(
                accountRepo.getById("deletedAccount"),
                userRepo.getById("testId"),
                true,
                false
        );
        underTest.save(testedWithDeletedAccount);

        // when
        ArrayList<AccountAccess> result = underTest.findAllHiddenByUserId(testedWithTestId.getUserId());
        //then
        assertEquals(1,result.size());
        assertEquals("testId", result.get(0).getUserId().getUserId());
        assertEquals("testIban2",result.get(0).getAccountId().getIban());
    }

    @Test
    void isFindAllByUserIdOrderedByBank(){
        // Bug encountered during development (used in the client application)

        // Given
        AccountAccess testedBank1a = new AccountAccess(
                accountRepo.getById("testIban"),
                userRepo.getById("testId"),
                true,
                false
        );
        underTest.save(testedBank1a);

        AccountAccess testedBank2 = new AccountAccess(
                accountRepo.getById("testIban2"),
                userRepo.getById("testId"),
                true,
                false
        );
        underTest.save(testedBank2);

        AccountAccess testedBank1b = new AccountAccess(
                accountRepo.getById("testIban3"),
                userRepo.getById("testId"),
                true,
                false
        );
        underTest.save(testedBank1b);

        // when
        ArrayList<AccountAccess> result = underTest.findAllByUserId(testedBank1a.getUserId());

        //Then
        assertEquals(
                testedBank1a.getAccountId().getSwift().getSwift(),
                result.get(0).getAccountId().getSwift().getSwift()
        );
        assertEquals(
                testedBank1b.getAccountId().getSwift().getSwift(),
                result.get(1).getAccountId().getSwift().getSwift()
        );
        assertEquals(
                testedBank2.getAccountId().getSwift().getSwift(),
                result.get(2).getAccountId().getSwift().getSwift()
        );
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

        AccountAccess accessToDeletedAccount = new AccountAccess(
                accountRepo.getById("deletedAccount"),
                userRepo.getById("testId"),
                true,
                false
        );
        underTest.save(accessToDeletedAccount);

        //When
        boolean shouldBeTrue = underTest.existsByUserIdAndAccountId(
                accessInDb.getUserId(),
                accessInDb.getAccountId()
        );

        boolean shouldBeFalse = underTest.existsByUserIdAndAccountId(
                userRepo.getById("test2Id"),
                accessInDb.getAccountId()
        );

        boolean shouldBeFalseCauseDeleted = underTest.existsByUserIdAndAccountId(
                accessToDeletedAccount.getUserId(),
                accessToDeletedAccount.getAccountId()
        );

        //Then
        assertTrue(shouldBeTrue);
        assertFalse(shouldBeFalse);
        assertFalse(shouldBeFalseCauseDeleted);

    }

    @Test
    void canGetAllCustomersInBank(){
        //Given
        String swift = "testSwift1";

        AccountAccess testedWithTestId = new AccountAccess(
                accountRepo.getById("testIban"),
                userRepo.getById("testId"),
                true,
                false
        );
        underTest.save(testedWithTestId);

        AccountAccess testWithDeletedAccount = new AccountAccess(
                accountRepo.getById("deletedAccount"),
                userRepo.getById("testId"),
                true,
                false
        );
        underTest.save(testWithDeletedAccount);

        //When
        List<User> result = underTest.getAllCustomersInBank(swift);

        //then
        // -- Should only return the user that has access to an account of the bank "testSwift"
        assertEquals(1,result.size());
        assertEquals("testId",result.get(0).getUserId());
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

    @Test
    void canGetAllOwners(){
        // Given
        String iban = "testIban";

        AccountAccess test1 = new AccountAccess(
                accountRepo.getById("testIban"),
                userRepo.getById("testId"),
                true,
                false
        );
        underTest.save(test1);

        AccountAccess test2 = new AccountAccess(
                accountRepo.getById("testIban"),
                userRepo.getById("test2Id"),
                true,
                false
        );
        underTest.save(test2);

        AccountAccess test3 = new AccountAccess(
                accountRepo.getById("testIban2"),
                userRepo.getById("testId"),
                true,
                false
        );
        underTest.save(test3);

        // When
        List<User> result = underTest.getAllOwners(accountRepo.getById(iban));

        // Then
        assertEquals(2,result.size());

    }
}