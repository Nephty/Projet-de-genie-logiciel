package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.*;
import com.example.demo.repository.AccountRepo;
import com.example.demo.repository.AccountTypeRepo;
import com.example.demo.repository.BankRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.request.AccountReq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    private AccountService underTest;

    @BeforeEach
    void setUp() {
        underTest = new AccountService(accountRepo,bankRepo,userRepo,accountTypeRepo);
    }

    @Test
    void getAccount() {
        //Given
        String iban = "testIban";
        Account expectedValue = new Account();

        when(accountRepo.findById(iban))
                .thenReturn(Optional.of(expectedValue));

        //When
        Account returnedValue = underTest.getAccount(iban);

        //Then
        verify(accountRepo).findById(iban);
        assertThat(returnedValue).isEqualTo(expectedValue);
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
        //When
        underTest.deleteAccount(iban);
        //Then
        verify(accountRepo).deleteById(iban);
    }

    @Test
    void canAddAccount() {
        //Given
        AccountReq accountReq = new AccountReq(
                "iban",
                "swift",
                "userId",
                1,
                false
        );

        Bank tmpBank = new Bank();
        tmpBank.setSwift(accountReq.getSwift());
        Optional<Bank> bank = Optional.of(tmpBank);
        when(bankRepo.findById(accountReq.getSwift()))
                .thenReturn(bank);

        User tmpUser = new User();
        tmpUser.setUserID(accountReq.getUserId());
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
                false
        );

        Bank tmpBank = new Bank();
        tmpBank.setSwift(accountReq.getSwift());
        Optional<Bank> bank = Optional.of(tmpBank);
        when(bankRepo.findById(accountReq.getSwift()))
                .thenReturn(bank);

        User tmpUser = new User();
        tmpUser.setUserID(accountReq.getUserId());
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
    void canChangeAccount(){
        //Given
        AccountReq accountReq = new AccountReq(
                "iban",
                "swift",
                "userId",
                1,
                false
        );

        Bank tmpBank = new Bank();
        tmpBank.setSwift(accountReq.getSwift());
        Optional<Bank> bank = Optional.of(tmpBank);
        when(bankRepo.findById(accountReq.getSwift()))
                .thenReturn(bank);

        User tmpUser = new User();
        tmpUser.setUserID(accountReq.getUserId());
        Optional<User> user = Optional.of(tmpUser);
        when(userRepo.findById(accountReq.getUserId()))
                .thenReturn(user);

        AccountType tmpType = new AccountType();
        tmpType.setAccountTypeId(accountReq.getAccountTypeId());
        Optional<AccountType> accountType = Optional.of(tmpType);
        when(accountTypeRepo.findById(accountReq.getAccountTypeId()))
                .thenReturn(accountType);

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
    void changeShouldThrowWhenBankNotFound(){
        //Given
        AccountReq accountReq = new AccountReq(
                "iban",
                "swift",
                "userId",
                1,
                false
        );

        //then
        assertThatThrownBy(()->underTest.changeAccount(accountReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(accountReq.getSwift());

        verify(accountRepo,never()).save(any());
    }

    @Test
    void changeShouldThrowWhenUserNotFound(){
        //Given
        AccountReq accountReq = new AccountReq(
                "iban",
                "swift",
                "userId",
                1,
                false
        );

        Bank tmpBank = new Bank();
        tmpBank.setSwift(accountReq.getSwift());
        Optional<Bank> bank = Optional.of(tmpBank);
        when(bankRepo.findById(accountReq.getSwift()))
                .thenReturn(bank);

        //then
        assertThatThrownBy(()->underTest.changeAccount(accountReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(accountReq.getUserId());

        verify(accountRepo,never()).save(any());
    }

    @Test
    void changeShouldThrowWhenAccountTypeNotFound(){
        //Given
        AccountReq accountReq = new AccountReq(
                "iban",
                "swift",
                "userId",
                1,
                false
        );

        Bank tmpBank = new Bank();
        tmpBank.setSwift(accountReq.getSwift());
        Optional<Bank> bank = Optional.of(tmpBank);
        when(bankRepo.findById(accountReq.getSwift()))
                .thenReturn(bank);

        User tmpUser = new User();
        tmpUser.setUserID(accountReq.getUserId());
        Optional<User> user = Optional.of(tmpUser);
        when(userRepo.findById(accountReq.getUserId()))
                .thenReturn(user);

        //then
        assertThatThrownBy(()->underTest.changeAccount(accountReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(accountReq.getAccountTypeId().toString());

        verify(accountRepo,never()).save(any());
    }
}