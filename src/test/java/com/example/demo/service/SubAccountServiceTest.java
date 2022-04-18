package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.Account;
import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.model.CurrencyType;
import com.example.demo.model.SubAccount;
import com.example.demo.repository.AccountRepo;
import com.example.demo.repository.CurrencyTypeRepo;
import com.example.demo.repository.SubAccountRepo;
import com.example.demo.request.SubAccountReq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubAccountServiceTest {

    @Mock
    private SubAccountRepo subAccountRepo;
    @Mock
    private AccountRepo accountRepo;
    @Mock
    private CurrencyTypeRepo currencyTypeRepo;

    private SubAccountService underTest;

    @BeforeEach
    void setUp() {
        underTest = new SubAccountService(subAccountRepo,accountRepo,currencyTypeRepo);
    }

    @Test
    void getSubAccount() {
        //given
        String iban = "iban";
        Integer currency = 0;

        //Given
        SubAccountReq subAccountReq = new SubAccountReq(
                iban,
                0,
                200.0,
                "EUR"
        );

        // -- Account --
        Account tmpAccount = new Account();
        tmpAccount.setIban(subAccountReq.getIban());


        // -- CurrencyType --
        CurrencyType tmpType = new CurrencyType();
        tmpType.setCurrencyId(subAccountReq.getCurrencyType());


        Optional<SubAccount> expectedValue = Optional.of(
                new SubAccount(tmpAccount,tmpType,subAccountReq.getCurrentBalance()));
        when(subAccountRepo.findById(new SubAccountPK(iban,currency)))
                .thenReturn(expectedValue);

        //When
        SubAccountReq returnedValue = underTest.getSubAccount(iban,currency);

        //Then
        verify(subAccountRepo).findById(new SubAccountPK(iban,currency));
        assertEquals(expectedValue.get().getIban().getIban(), returnedValue.getIban());
    }

    @Test
    void getShouldThrowWhenIdNotFound(){
        //given
        String iban = "iban";
        Integer currency = 0;

        //then
        assertThatThrownBy(() -> underTest.getSubAccount(iban,currency))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining(iban + " : " + currency);

    }

    @Test
    void deleteSubAccount() {
        //given
        String iban = "iban";
        Integer currency = 0;

        //When
        underTest.deleteSubAccount(iban,currency);

        //then
        verify(subAccountRepo).deleteById(new SubAccountPK(iban,currency));
    }

    @Test
    void addSubAccount() {
        //Given
        SubAccountReq subAccountReq = new SubAccountReq(
                "iban",
                0,
                200.0,
                "EUR"
        );

            // -- Account --
        Account tmpAccount = new Account();
        tmpAccount.setDeleted(false);
        tmpAccount.setIban(subAccountReq.getIban());
        Optional<Account> account = Optional.of(tmpAccount);
        when(accountRepo.safeFindById(subAccountReq.getIban()))
                .thenReturn(account);

            // -- CurrencyType --
        CurrencyType tmpType = new CurrencyType();
        tmpType.setCurrencyId(subAccountReq.getCurrencyType());
        Optional<CurrencyType> currencyType = Optional.of(tmpType);
        when(currencyTypeRepo.findById(subAccountReq.getCurrencyType()))
                .thenReturn(currencyType);

        //When
        underTest.addSubAccount(subAccountReq);

        //Then
        ArgumentCaptor<SubAccount> argumentCaptor = ArgumentCaptor.forClass(SubAccount.class);
        verify(subAccountRepo).save(argumentCaptor.capture());
        SubAccount captorValue = argumentCaptor.getValue();

            //The user saved is the good one.
        assertEquals(tmpAccount,captorValue.getIban());
        assertEquals(tmpType,captorValue.getCurrencyType());
        assertEquals(subAccountReq.getCurrentBalance(),captorValue.getCurrentBalance());
    }

    @Test
    void addShouldThrowWhenAccountIdNotFound(){
        //Given
        SubAccountReq subAccountReq = new SubAccountReq(
                "iban",
                0,
                200.0,
                "EUR"
        );

        //then
        assertThatThrownBy(() -> underTest.addSubAccount(subAccountReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(subAccountReq.getIban());
        verify(subAccountRepo,never()).save(any());
    }

    @Test
    void addShouldThrowWhenCurrencyTypeNotFound(){
        //Given
        SubAccountReq subAccountReq = new SubAccountReq(
                "iban",
                0,
                200.0,
                "EUR"
        );

            // -- Account --
        Account tmpAccount = new Account();
        tmpAccount.setDeleted(false);
        tmpAccount.setIban(subAccountReq.getIban());
        Optional<Account> account = Optional.of(tmpAccount);
        when(accountRepo.safeFindById(subAccountReq.getIban()))
                .thenReturn(account);

        //then
        assertThatThrownBy(() -> underTest.addSubAccount(subAccountReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(subAccountReq.getCurrencyType().toString());
        verify(subAccountRepo,never()).save(any());
    }

    @Test
    void changeSubAccount() {
        //Given
        SubAccountReq subAccountReq = new SubAccountReq(
                "iban",
                0,
                200.0,
                "EUR"
        );

        // -- Account --
        Account tmpAccount = new Account();
        tmpAccount.setDeleted(false);
        tmpAccount.setIban(subAccountReq.getIban());

        // -- CurrencyType --
        CurrencyType tmpType = new CurrencyType();
        tmpType.setCurrencyId(subAccountReq.getCurrencyType());

        SubAccount subAccount = new SubAccount(subAccountReq);
        subAccount.setIban(tmpAccount);
        subAccount.setCurrencyType(tmpType);
        when(subAccountRepo.findById(new SubAccountPK(subAccountReq.getIban(), subAccountReq.getCurrencyType())))
                .thenReturn(Optional.of(subAccount));

        //When
        underTest.changeSubAccount(subAccountReq);

        //Then
        ArgumentCaptor<SubAccount> argumentCaptor = ArgumentCaptor.forClass(SubAccount.class);
        verify(subAccountRepo).save(argumentCaptor.capture());
        SubAccount captorValue = argumentCaptor.getValue();

        //The user saved is the good one.
        assertEquals(tmpAccount,captorValue.getIban());
        assertEquals(tmpType,captorValue.getCurrencyType());
        assertEquals(subAccountReq.getCurrentBalance(),captorValue.getCurrentBalance());
    }

    @Test
    void changeShouldThrowWhenSubAccountNotFound(){
        //Given
        SubAccountReq subAccountReq = new SubAccountReq(
                "iban",
                0,
                200.0,
                "EUR"
        );

        //then
        assertThatThrownBy(() -> underTest.changeSubAccount(subAccountReq))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining(subAccountReq.getIban());
        verify(subAccountRepo,never()).save(any());
    }
}