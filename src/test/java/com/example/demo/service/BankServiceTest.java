package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.exception.throwables.UserAlreadyExist;
import com.example.demo.model.Bank;
import com.example.demo.model.CurrencyType;
import com.example.demo.other.Sender;
import com.example.demo.repository.AccountAccessRepo;
import com.example.demo.repository.BankRepo;
import com.example.demo.repository.CurrencyTypeRepo;
import com.example.demo.request.BankReq;
import com.example.demo.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @Mock
    private BankRepo bankRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CurrencyTypeRepo currencyTypeRepo;

    private AccountAccessRepo accountAccessRepo;

    private BankService underTest;

    @BeforeEach
    void setUp() {
        underTest = new BankService(bankRepo,passwordEncoder,currencyTypeRepo, accountAccessRepo);
    }

    @Test
    void canAddBank() {
        //Given
        BankReq bankReq = new BankReq(
                "swift",
                "name",
                "login",
                "password",
                "address",
                "country",
                0
        );

            // -- CurrencyType --
        CurrencyType tmpType = new CurrencyType();
        tmpType.setCurrencyId(bankReq.getDefaultCurrencyType());
        Optional<CurrencyType> currencyType = Optional.of(tmpType);
        when(currencyTypeRepo.findById(bankReq.getDefaultCurrencyType()))
                .thenReturn(currencyType);

        when(passwordEncoder.encode(bankReq.getPassword()))
                .thenReturn("EncodedPassword");

        //When
        underTest.addBank(bankReq);

        //Then
        ArgumentCaptor<Bank> argumentCaptor = ArgumentCaptor.forClass(Bank.class);
        verify(bankRepo).save(argumentCaptor.capture());
        Bank captorValue = argumentCaptor.getValue();

            // Is the password encoded correctly
        verify(passwordEncoder).encode("password");
        assertEquals("EncodedPassword",captorValue.getPassword());
            //The user saved is the good one.
        assertEquals(tmpType,captorValue.getDefaultCurrencyType());
        assertEquals(bankReq.getSwift(),captorValue.getSwift());
        assertEquals(bankReq.getName(),captorValue.getName());
        assertEquals(bankReq.getAddress(),captorValue.getAddress());
        assertEquals(bankReq.getCountry(),captorValue.getCountry());
    }

    @Test
    void addShouldThrowWhenIdAlreadyExists(){
        //Given
        String id = "swift";
        BankReq bankReq = new BankReq(
                id,
                "name",
                "login",
                "password",
                "address",
                "country",
                0
        );
        when(bankRepo.existsById(id)).thenReturn(true);

        //Then
        assertThatThrownBy(() -> underTest.addBank(bankReq))
                .isInstanceOf(UserAlreadyExist.class)
                .hasMessageContaining("SWIFT");

        verify(bankRepo,never()).save(any());
    }

    @Test
    void addShouldThrowWhenNameAlreadyExists(){
        //Given
        String name = "name";
        BankReq bankReq = new BankReq(
                "swift",
                name,
                "login",
                "password",
                "address",
                "country",
                0
        );
        when(bankRepo.existsByName(name)).thenReturn(true);

        //then
        assertThatThrownBy(() -> underTest.addBank(bankReq))
                .isInstanceOf(UserAlreadyExist.class)
                .hasMessageContaining("NAME");
        verify(bankRepo,never()).save(any());
    }

    @Test
    void addShouldThrowWhenCurrencyTypeNotFound(){
        //Given
        Integer currencyType = 0;
        BankReq bankReq = new BankReq(
                "swift",
                "name",
                "login",
                "password",
                "address",
                "country",
                currencyType
        );

        //then
        assertThatThrownBy(() -> underTest.addBank(bankReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(currencyType.toString());
        verify(bankRepo,never()).save(any());
    }

    @Test
    void canDeleteBank() {
        //Given
        String swift = "swift";

        //when
        underTest.deleteBank(swift);

        //then
        verify(bankRepo).deleteById(swift);
    }

    @Test
    void changeBank() {
        //Given
        BankReq bankReq = new BankReq(
                null,
                "name",
                "login",
                "password",
                "address",
                "country",
                0
        );
        Sender sender = new Sender("swift", Role.BANK);

            // -- Bank --
        Optional<Bank> bank = Optional.of(new Bank(bankReq));
        bank.get().setSwift(sender.getId());
        when(bankRepo.findById(sender.getId())).thenReturn(bank);

            // -- Currency --
        CurrencyType tmpType = new CurrencyType();
        tmpType.setCurrencyId(bankReq.getDefaultCurrencyType());
        Optional<CurrencyType> currencyType = Optional.of(tmpType);
        when(currencyTypeRepo.findById(bankReq.getDefaultCurrencyType()))
                .thenReturn(currencyType);

            // -- Password --
        when(passwordEncoder.encode(bankReq.getPassword()))
                .thenReturn("EncodedPassword");

        //When
        underTest.changeBank(sender,bankReq);

        //Then
        ArgumentCaptor<Bank> userArgumentCaptor = ArgumentCaptor.forClass(Bank.class);
        verify(bankRepo).save(userArgumentCaptor.capture());
        Bank captorValue = userArgumentCaptor.getValue();

            // Is the password encoded correctly
        verify(passwordEncoder).encode("password");
        assertEquals("EncodedPassword",captorValue.getPassword());

            //The user saved is the good one.
        assertEquals(tmpType,captorValue.getDefaultCurrencyType());
        assertEquals(sender.getId(),captorValue.getSwift());
        assertEquals(bankReq.getName(),captorValue.getName());
        assertEquals(bankReq.getAddress(),captorValue.getAddress());
        assertEquals(bankReq.getCountry(),captorValue.getCountry());
    }

    @Test
    void changeShouldThrowWhenSenderIdNotFound(){
        //Given
        BankReq bankReq = new BankReq(
                null,
                "name",
                "login",
                "password",
                "address",
                "country",
                0
        );
        Sender sender = new Sender("swift", Role.BANK);

        //Then
        assertThatThrownBy(() -> underTest.changeBank(sender,bankReq))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining(sender.getId());
        verify(bankRepo,never()).save(any());
    }

    @Test
    void changeShouldThrowWhenCurrencyTypeNotFound(){
        //Given
        BankReq bankReq = new BankReq(
                null,
                "name",
                "login",
                "password",
                "address",
                "country",
                0
        );
        Sender sender = new Sender("swift", Role.BANK);

            // -- Bank --
        Optional<Bank> bank = Optional.of(new Bank(bankReq));
        bank.get().setSwift(sender.getId());
        when(bankRepo.findById(sender.getId())).thenReturn(bank);

        //then
        assertThatThrownBy(() -> underTest.changeBank(sender,bankReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(bankReq.getDefaultCurrencyType().toString());
        verify(bankRepo,never()).save(any());
    }

    @Test
    void getBank() {
        //Given
        String swift = "swift";
        Optional<Bank> bank = Optional.of(new Bank());
        when(bankRepo.findById(swift)).thenReturn(bank);

        //when
        underTest.getBank(swift);
        //then
        verify(bankRepo).findById(swift);
    }

    @Test
    @Disabled
    void getByLogin() {
        // TODO : delete login
    }

    @Test
    void getAllBanks() {
        //When
        underTest.getAllBanks();
        //Then
        verify(bankRepo).findAll();
    }
}