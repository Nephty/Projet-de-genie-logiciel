package com.example.demo.service;

import com.example.demo.exception.throwables.AuthorizationException;
import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.request.AccountReq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private UserRepo userRepo;
    @Mock
    private AccountRepo accountRepo;
    @Mock
    private BankRepo bankRepo;
    @Mock
    private AccountTypeRepo accountTypeRepo;
    @Mock
    private SubAccountRepo subAccountRepo;
    @Mock
    private AccountAccessRepo accountAccessRepo;

    private AccountService underTest;

    @BeforeEach
    void setUp() {
        underTest = new AccountService(accountRepo,bankRepo,userRepo,accountTypeRepo,subAccountRepo, accountAccessRepo);
    }

    @Test
    void getAccount() {
        //Given
        AccountReq accountReq = new AccountReq(
                "iban",
                "swift",
                "userId",
                1,
                false,
                "name",
                "lastname",
                null,
                Date.valueOf(LocalDate.now()),
                false

        );

        Bank tmpBank = new Bank();
        tmpBank.setSwift(accountReq.getSwift());
        tmpBank.setDefaultCurrencyType(new CurrencyType(0,"EUR"));

        User tmpUser = new User();
        tmpUser.setUserId(accountReq.getUserId());

        AccountType tmpType = new AccountType();
        tmpType.setAccountTypeId(accountReq.getAccountTypeId());

        Account expectedValue = new Account(accountReq);
        expectedValue.setAccountTypeId(tmpType);
        expectedValue.setSwift(tmpBank);
        expectedValue.setUserId(tmpUser);


        when(accountRepo.findById(accountReq.getIban()))
                .thenReturn(Optional.of(expectedValue));

        //When
        AccountReq returnedValue = underTest.getAccount(accountReq.getIban());

        //Then
        verify(accountRepo).findById(accountReq.getIban());
        assertThat(returnedValue).isEqualTo(new AccountReq(expectedValue));
    }

    @Test
    void getShouldThrowWhenIdNotFound(){
        //Given
        String iban = "testIban";

        //Then
        assertThatThrownBy(() -> underTest.getAccount(iban))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("iban: "+iban);
    }

    @Test
    void canDeleteAccount() {
        //Given
        String iban = "testIban";
        Account acc = new Account();
        acc.setIban(iban);

        when(accountRepo.safeFindById(iban)).thenReturn(Optional.of(acc));
        //When
        underTest.deleteAccount(iban);
        //Then
        ArgumentCaptor<Account> userArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo).save(userArgumentCaptor.capture());
        Account captorValue = userArgumentCaptor.getValue();

        assertEquals(iban,captorValue.getIban());
        assertEquals(true,captorValue.getDeleted());
    }

    @Test
    void deleteAccountShouldThrowWhenAccountNotFound(){
        // Given
        String iban = "testIBan";

        // Then
        assertThatThrownBy(()->underTest.deleteAccount(iban))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("iban: "+iban);

        verify(accountRepo,never()).save(any());
    }

    @Test
    void canAddAccount() {
        //Given
        AccountReq accountReq = new AccountReq(
                "iban",
                "swift",
                "userId",
                1,
                false,
                "name",
                "lastname",
                null,
                Date.valueOf(LocalDate.now()),
                false
        );

        Bank tmpBank = new Bank();
        tmpBank.setSwift(accountReq.getSwift());
        tmpBank.setDefaultCurrencyType(new CurrencyType(0,"EUR"));
        Optional<Bank> bank = Optional.of(tmpBank);
        when(bankRepo.findById(accountReq.getSwift()))
                .thenReturn(bank);

        User tmpUser = new User();
        tmpUser.setUserId(accountReq.getUserId());
        tmpUser.setBirthdate(Date.valueOf(LocalDate.of(2002,10,31)));
        Optional<User> user = Optional.of(tmpUser);
        when(userRepo.findById(accountReq.getUserId()))
                .thenReturn(user);

        AccountType tmpType = new AccountType();
        tmpType.setAccountTypeId(accountReq.getAccountTypeId());
        Optional<AccountType> accountType = Optional.of(tmpType);
        when(accountTypeRepo.findById(accountReq.getAccountTypeId()))
                .thenReturn(accountType);

        //When
        underTest.addAccount(accountReq);

        //Then
        ArgumentCaptor<Account> userArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo).save(userArgumentCaptor.capture());
        Account captorValue = userArgumentCaptor.getValue();

        assertEquals(tmpBank,captorValue.getSwift());
        assertEquals(tmpUser,captorValue.getUserId());
        assertEquals(tmpType,captorValue.getAccountTypeId());
        assertEquals(accountReq.getIban(),captorValue.getIban());
        assertEquals(accountReq.getPayment(),captorValue.getPayment());
    }

    @Test
    void addShouldThrowWhenBankNotFound(){
        //Given
        AccountReq accountReq = new AccountReq(
                "iban",
                "swift",
                "userId",
                1,
                false,
                "name",
                "lastname",
                null,
                Date.valueOf(LocalDate.now()),
                false
        );

        //then
        assertThatThrownBy(()->underTest.addAccount(accountReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(accountReq.getSwift());

        verify(accountRepo,never()).save(any());
    }

    @Test
    void addShouldThrowWhenUserNotFound(){
        //Given
        AccountReq accountReq = new AccountReq(
                "iban",
                "swift",
                "userId",
                1,
                false,
                "name",
                "lastname",
                null,
                Date.valueOf(LocalDate.now()),
                false
        );

        Bank tmpBank = new Bank();
        tmpBank.setSwift(accountReq.getSwift());
        Optional<Bank> bank = Optional.of(tmpBank);
        when(bankRepo.findById(accountReq.getSwift()))
                .thenReturn(bank);

        //then
        assertThatThrownBy(()->underTest.addAccount(accountReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(accountReq.getUserId());

        verify(accountRepo,never()).save(any());
    }

    @Test
    void addShouldThrowWhenAccountTypeNotFound(){
        //Given
        AccountReq accountReq = new AccountReq(
                "iban",
                "swift",
                "userId",
                1,
                false,
                "name",
                "lastname",
                null,
                Date.valueOf(LocalDate.now()),
                false
        );

        Bank tmpBank = new Bank();
        tmpBank.setSwift(accountReq.getSwift());
        Optional<Bank> bank = Optional.of(tmpBank);
        when(bankRepo.findById(accountReq.getSwift()))
                .thenReturn(bank);

        User tmpUser = new User();
        tmpUser.setUserId(accountReq.getUserId());
        Optional<User> user = Optional.of(tmpUser);
        when(userRepo.findById(accountReq.getUserId()))
                .thenReturn(user);

        //then
        assertThatThrownBy(()->underTest.addAccount(accountReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(accountReq.getAccountTypeId().toString());

        verify(accountRepo,never()).save(any());
    }

    @Test
    @Disabled
    void addShouldThrowWhenOwnerUnderAge() {
        // TODO: 4/18/22 test
    }

    @Test
    @Disabled
    void addShouldThrowWhenOverageForYoungAccount() {
        // TODO: 4/18/22 test
    }

    @Test
    void canChangeAccount(){
        //Given
        AccountReq accountReq = new AccountReq(
                "iban",
                "swift",
                "userId",
                1,
                false,
                "name",
                "lastname",
                null,
                Date.valueOf(LocalDate.now()),
                false
        );

        Bank tmpBank = new Bank();
        tmpBank.setSwift(accountReq.getSwift());

        User tmpUser = new User();
        tmpUser.setUserId(accountReq.getUserId());

        AccountType tmpType = new AccountType();
        tmpType.setAccountTypeId(accountReq.getAccountTypeId());

        Account account = new Account(accountReq);
        account.setSwift(tmpBank);
        account.setUserId(tmpUser);
        account.setAccountTypeId(tmpType);
        when(accountRepo.safeFindById(accountReq.getIban()))
                .thenReturn(Optional.of(account));


        //When
        underTest.changeAccount(accountReq);

        //Then
        ArgumentCaptor<Account> userArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo).save(userArgumentCaptor.capture());
        Account captorValue = userArgumentCaptor.getValue();

        assertEquals(tmpBank,captorValue.getSwift());
        assertEquals(tmpUser,captorValue.getUserId());
        assertEquals(tmpType,captorValue.getAccountTypeId());
        assertEquals(accountReq.getIban(),captorValue.getIban());
        assertEquals(accountReq.getPayment(),captorValue.getPayment());
    }

    @Test
    void changeShouldThrowWhenAccountNotFound(){
        //Given
        AccountReq accountReq = new AccountReq(
                "iban",
                "swift",
                "userId",
                1,
                false,
                "name",
                "lastname",
                null,
                Date.valueOf(LocalDate.now()),
                false
        );

        //then
        assertThatThrownBy(()->underTest.changeAccount(accountReq))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining(accountReq.toString());

        verify(accountRepo,never()).save(any());
    }

    @Test
    void changeShouldThrowWhenFixedAccountAndPayment() {
        // Throws if it's a fixed account and payment = true
        //Given
        AccountReq accountReq = new AccountReq(
                "iban",
                "swift",
                "userId",
                4,
                true,
                "name",
                "lastname",
                null,
                Date.valueOf(LocalDate.now()),
                false
        );

        Bank tmpBank = new Bank();
        tmpBank.setSwift(accountReq.getSwift());

        User tmpUser = new User();
        tmpUser.setUserId(accountReq.getUserId());

        AccountType tmpType = new AccountType();
        tmpType.setAccountTypeId(accountReq.getAccountTypeId());

        Account account = new Account(accountReq);
        account.setSwift(tmpBank);
        account.setUserId(tmpUser);
        account.setAccountTypeId(tmpType);
        when(accountRepo.safeFindById(accountReq.getIban()))
                .thenReturn(Optional.of(account));


        //then
        assertThatThrownBy(()->underTest.changeAccount(accountReq))
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining("This is a fixed account you can't allow payment to it");

        verify(accountRepo,never()).save(any());
    }
    
    @Test
    @Disabled
    void changeShouldThrowWhenYoungAccountAndPaymentButNoAdultCoOwner() {
        // TODO: 4/18/22 test 
    }
}