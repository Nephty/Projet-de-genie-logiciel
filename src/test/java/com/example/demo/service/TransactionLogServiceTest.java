package com.example.demo.service;

import com.example.demo.exception.throwables.AuthorizationException;
import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.model.*;
import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.other.Sender;
import com.example.demo.repository.AccountAccessRepo;
import com.example.demo.repository.SubAccountRepo;
import com.example.demo.repository.TransactionLogRepo;
import com.example.demo.repository.TransactionTypeRepo;
import com.example.demo.request.NotificationReq;
import com.example.demo.request.SubAccountReq;
import com.example.demo.request.TransactionReq;
import com.example.demo.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionLogServiceTest {

    @Mock
    private TransactionLogRepo transactionLogRepo;
    @Mock
    private SubAccountRepo subAccountRepo;
    @Mock
    private TransactionTypeRepo transactionTypeRepo;
    @Mock
    private AccountAccessRepo accountAccessRepo;
    @Mock
    private NotificationService notificationService;

    private TransactionLogService underTest;

    @BeforeEach
    void setUp() {
        underTest = new TransactionLogService(
                transactionLogRepo,
                subAccountRepo,
                transactionTypeRepo,
                accountAccessRepo,
                notificationService
        );
    }

    @Test
    void canAddTransaction() {
        // Given
        String id = "userId";
        Sender sender = new Sender("",Role.USER); // id !=
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("senderIban");
        transactionReq.setRecipientIban("receiverIban");
        transactionReq.setCurrencyId(0);
        transactionReq.setTransactionAmount(10.0);

        CurrencyType currencyType = new CurrencyType(0,"EUR");

        // -- Account SENDER --
        Account accountSender = new Account();
        accountSender.setPayment(true);
        accountSender.setDeleted(false);
        User user = new User();
        user.setUserId(id);
        accountSender.setUserId(user);

        // -- Account RECEIVER --
        Account accountReceiver = new Account();
        accountReceiver.setDeleted(false);

        // -- SubAccount SENDER --
        SubAccount subAccountSender = new SubAccount(
                accountSender,
                currencyType,
                150.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountSender));

        // -- SubAccount RECEIVER --
        SubAccount subAccountReceiver = new SubAccount(
                accountReceiver,
                currencyType,
                100.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getRecipientIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountReceiver));

        // -- TransactionType --
        TransactionType transactionType = new TransactionType(0,"name",0.0);
        when(transactionTypeRepo.findById(1)).thenReturn(Optional.of(transactionType));


        // -- AccountAccess Sender --
        AccountAccess senderAccess = new AccountAccess(
                accountSender,
                user,
                true, // access is false
                false
        );
        when(accountAccessRepo.findById(
                new AccountAccessPK(subAccountSender.getIban().getIban(), sender.getId())))
                .thenReturn(Optional.of(senderAccess));

        // When
        underTest.addTransaction(sender,transactionReq);

        // Then
        ArgumentCaptor<TransactionLog> transactionCaptor = ArgumentCaptor.forClass(TransactionLog.class);
        verify(transactionLogRepo,times(2)).save(transactionCaptor.capture());
        List<TransactionLog> transactionCaptured = transactionCaptor.getAllValues();


        // -- Next id working --
        assertEquals(1,transactionCaptured.get(0).getTransactionId());
        assertEquals(1,transactionCaptured.get(1).getTransactionId());

        // -- RECEIVER --
        TransactionLog transactionReceiverCaptured = !transactionCaptured.get(0).getIsSender()
                ? transactionCaptured.get(0) : transactionCaptured.get(1);
        assertFalse(transactionReceiverCaptured.getProcessed());
        assertEquals(subAccountReceiver,transactionReceiverCaptured.getSubAccount());
        assertEquals(transactionReq.getTransactionAmount(),transactionReceiverCaptured.getTransactionAmount());

        // -- SENDER --
        TransactionLog transactionSenderCaptured = transactionCaptured.get(0).getIsSender()
                ? transactionCaptured.get(0) : transactionCaptured.get(1);
        assertFalse(transactionSenderCaptured.getProcessed());
        assertEquals(subAccountSender,transactionSenderCaptured.getSubAccount());
        assertEquals(transactionReq.getTransactionAmount(),transactionSenderCaptured.getTransactionAmount());

    }

    @Test
    void addTransactionShouldThrowWhenSenderIsReceiver(){
        // Given
        String id = "userId";
        Sender sender = new Sender(id,Role.USER);
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("senderIban");
        transactionReq.setRecipientIban("senderIban");
        transactionReq.setCurrencyId(0);
        transactionReq.setTransactionAmount(10.0);

        // Then
        assertThatThrownBy(()-> underTest.addTransaction(sender,transactionReq))
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining("You can't make a transaction to the same account you emitted it");

    }

    @Test
    void addTransactionShouldThrowWhenSenderNotFound(){
        // Given
        String id = "userId";
        Sender sender = new Sender(id,Role.USER);
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("senderIban");
        transactionReq.setRecipientIban("receiverIban");
        transactionReq.setCurrencyId(0);
        transactionReq.setTransactionAmount(10.0);

        // Then
        assertThatThrownBy(()-> underTest.addTransaction(sender,transactionReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(
                        "subAccount: " + transactionReq.getSenderIban() + " : " + transactionReq.getCurrencyId()
                );
    }

    @Test
    void addTransactionShouldThrowWhenSenderAccountDeleted(){
        // Given
        String id = "userId";
        Sender sender = new Sender(id,Role.USER);
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("senderIban");
        transactionReq.setRecipientIban("receiverIban");
        transactionReq.setCurrencyId(0);
        transactionReq.setTransactionAmount(10.0);

        CurrencyType currencyType = new CurrencyType(0,"EUR");

        // -- Account SENDER --
        Account accountSender = new Account();
        accountSender.setPayment(true);
        accountSender.setDeleted(true);
        User user = new User();
        user.setUserId(id);
        accountSender.setUserId(user);

        // -- SubAccount SENDER --
        SubAccount subAccountSender = new SubAccount(
                accountSender,
                currencyType,
                150.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountSender));

        // Then
        assertThatThrownBy(()-> underTest.addTransaction(sender,transactionReq))
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining(
                        "Can't make a transaction from a deleted Account: " + subAccountSender.getIban()
                );
    }

    @Test
    void addTransactionShouldThrowWhenReceiverNotFound(){
        // Given
        String id = "userId";
        Sender sender = new Sender(id,Role.USER);
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("senderIban");
        transactionReq.setRecipientIban("receiverIban");
        transactionReq.setCurrencyId(0);
        transactionReq.setTransactionAmount(10.0);

        CurrencyType currencyType = new CurrencyType(0,"EUR");

        // -- Account SENDER --
        Account accountSender = new Account();
        accountSender.setPayment(true);
        accountSender.setDeleted(false);
        User user = new User();
        user.setUserId(id);
        accountSender.setUserId(user);

        // -- SubAccount SENDER --
        SubAccount subAccountSender = new SubAccount(
                accountSender,
                currencyType,
                150.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
                )).thenReturn(Optional.of(subAccountSender));

        // Then
        assertThatThrownBy(()-> underTest.addTransaction(sender,transactionReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(
                        "subAccount: " + transactionReq.getRecipientIban() + " : " + transactionReq.getCurrencyId()
                );
    }

    @Test
    void addTransactionShouldThrowWhenReceiverAccountDeleted(){
        // Given
        String id = "userId";
        Sender sender = new Sender(id,Role.USER);
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("senderIban");
        transactionReq.setRecipientIban("receiverIban");
        transactionReq.setCurrencyId(0);
        transactionReq.setTransactionAmount(10.0);

        CurrencyType currencyType = new CurrencyType(0,"EUR");

        // -- Account SENDER --
        Account accountSender = new Account();
        accountSender.setPayment(true);
        accountSender.setDeleted(false);
        User user = new User();
        user.setUserId(id);
        accountSender.setUserId(user);

        // -- Account RECEIVER --
        Account accountReceiver = new Account();
        accountReceiver.setDeleted(true);

        // -- SubAccount SENDER --
        SubAccount subAccountSender = new SubAccount(
                accountSender,
                currencyType,
                150.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountSender));

        // -- SubAccount RECEIVER --
        SubAccount subAccountReceiver = new SubAccount(
                accountReceiver,
                currencyType,
                100.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getRecipientIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountReceiver));


        // Then
        assertThatThrownBy(()-> underTest.addTransaction(sender,transactionReq))
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining(
                        "Can't make a transaction to a deleted Account"
                );
    }

    @Test
    void addTransactionShouldThrowWhenTransactionTypeNotFound(){
        // Given
        String id = "userId";
        Sender sender = new Sender(id,Role.USER);
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("senderIban");
        transactionReq.setRecipientIban("receiverIban");
        transactionReq.setCurrencyId(0);
        transactionReq.setTransactionAmount(10.0);

        CurrencyType currencyType = new CurrencyType(0,"EUR");

        // -- Account SENDER --
        Account accountSender = new Account();
        accountSender.setPayment(true);
        accountSender.setDeleted(false);
        User user = new User();
        user.setUserId(id);
        accountSender.setUserId(user);

        // -- Account RECEIVER --
        Account accountReceiver = new Account();
        accountReceiver.setDeleted(false);

        // -- SubAccount SENDER --
        SubAccount subAccountSender = new SubAccount(
                accountSender,
                currencyType,
                150.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountSender));

        // -- SubAccount RECEIVER --
        SubAccount subAccountReceiver = new SubAccount(
                accountReceiver,
                currencyType,
                100.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getRecipientIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountReceiver));


        // Then
        assertThatThrownBy(()-> underTest.addTransaction(sender,transactionReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(
                        "transId:" + transactionReq.getTransactionTypeId().toString()
                );
    }

    @Test
    void instantiateTransactionShouldThrowWhenAccountCantMakePayment(){
        // Given
        String id = "userId";
        Sender sender = new Sender(id,Role.USER);
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("senderIban");
        transactionReq.setRecipientIban("receiverIban");
        transactionReq.setCurrencyId(0);
        transactionReq.setTransactionAmount(10.0);

        CurrencyType currencyType = new CurrencyType(0,"EUR");

        // -- Account SENDER --
        Account accountSender = new Account();
        accountSender.setPayment(false);
        accountSender.setDeleted(false);

        // -- Account RECEIVER --
        Account accountReceiver = new Account();
        accountReceiver.setDeleted(false);

        // -- SubAccount SENDER --
        SubAccount subAccountSender = new SubAccount(
                accountSender,
                currencyType,
                150.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountSender));

        // -- SubAccount RECEIVER --
        SubAccount subAccountReceiver = new SubAccount(
                accountReceiver,
                currencyType,
                100.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getRecipientIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountReceiver));

        // -- TransactionType --
        TransactionType transactionType = new TransactionType(0,"name",0.0);
        when(transactionTypeRepo.findById(1)).thenReturn(Optional.of(transactionType));


        // Then
        assertThatThrownBy(()-> underTest.addTransaction(sender,transactionReq))
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining("This account can't make payment");
    }

    @Test
    void instantiateTransactionShouldThrowWhenAmountLowerOrEqualToZero(){
        // Given
        String id = "userId";
        Sender sender = new Sender(id,Role.USER);
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("senderIban");
        transactionReq.setRecipientIban("receiverIban");
        transactionReq.setCurrencyId(0);
        transactionReq.setTransactionAmount(-10.0); // <= 0

        CurrencyType currencyType = new CurrencyType(0,"EUR");

        // -- Account SENDER --
        Account accountSender = new Account();
        accountSender.setPayment(true);
        accountSender.setDeleted(false);

        // -- Account RECEIVER --
        Account accountReceiver = new Account();
        accountReceiver.setDeleted(false);

        // -- SubAccount SENDER --
        SubAccount subAccountSender = new SubAccount(
                accountSender,
                currencyType,
                150.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountSender));

        // -- SubAccount RECEIVER --
        SubAccount subAccountReceiver = new SubAccount(
                accountReceiver,
                currencyType,
                100.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getRecipientIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountReceiver));

        // -- TransactionType --
        TransactionType transactionType = new TransactionType(0,"name",0.0);
        when(transactionTypeRepo.findById(1)).thenReturn(Optional.of(transactionType));


        // Then
        assertThatThrownBy(()-> underTest.addTransaction(sender,transactionReq))
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining("Can't make transaction lower or equal to 0");
    }

    @Test
    void instantiateTransactionShouldThrowWhenAccountDontHaveEnoughMoney() {
        // Given
        String id = "userId";
        Sender sender = new Sender(id,Role.USER);
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("senderIban");
        transactionReq.setRecipientIban("receiverIban");
        transactionReq.setCurrencyId(0);
        transactionReq.setTransactionAmount(1000.0);

        CurrencyType currencyType = new CurrencyType(0,"EUR");

        // -- Account SENDER --
        Account accountSender = new Account();
        accountSender.setPayment(true);
        accountSender.setDeleted(false);

        // -- Account RECEIVER --
        Account accountReceiver = new Account();
        accountReceiver.setDeleted(false);

        // -- SubAccount SENDER --
        SubAccount subAccountSender = new SubAccount(
                accountSender,
                currencyType,
                150.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountSender));

        // -- SubAccount RECEIVER --
        SubAccount subAccountReceiver = new SubAccount(
                accountReceiver,
                currencyType,
                100.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getRecipientIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountReceiver));

        // -- TransactionType --
        TransactionType transactionType = new TransactionType(0,"name",0.0);
        when(transactionTypeRepo.findById(1)).thenReturn(Optional.of(transactionType));


        // Then
        assertThatThrownBy(()-> underTest.addTransaction(sender,transactionReq))
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining("You don't have enough money");
    }

    @Test
    void instantiateTransactionShouldThrowWhenUserDontHaveAccessToAccount(){
        // Given
        String id = "userId";
        Sender sender = new Sender("",Role.USER); // id !=
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("senderIban");
        transactionReq.setRecipientIban("receiverIban");
        transactionReq.setCurrencyId(0);
        transactionReq.setTransactionAmount(10.0);

        CurrencyType currencyType = new CurrencyType(0,"EUR");

        // -- Account SENDER --
        Account accountSender = new Account();
        accountSender.setPayment(true);
        accountSender.setDeleted(false);
        User user = new User();
        user.setUserId(id);
        accountSender.setUserId(user);

        // -- Account RECEIVER --
        Account accountReceiver = new Account();
        accountReceiver.setDeleted(false);

        // -- SubAccount SENDER --
        SubAccount subAccountSender = new SubAccount(
                accountSender,
                currencyType,
                150.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountSender));

        // -- SubAccount RECEIVER --
        SubAccount subAccountReceiver = new SubAccount(
                accountReceiver,
                currencyType,
                100.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getRecipientIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountReceiver));

        // -- TransactionType --
        TransactionType transactionType = new TransactionType(0,"name",0.0);
        when(transactionTypeRepo.findById(1)).thenReturn(Optional.of(transactionType));

        // Then
        assertThatThrownBy(()-> underTest.addTransaction(sender,transactionReq))
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining("You don't have access to this account");
    }

    @Test
    void instantiateTransactionShouldThrowWhenAccessDisabled(){
        // Given
        String id = "userId";
        Sender sender = new Sender("",Role.USER); // id !=
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("senderIban");
        transactionReq.setRecipientIban("receiverIban");
        transactionReq.setCurrencyId(0);
        transactionReq.setTransactionAmount(10.0);

        CurrencyType currencyType = new CurrencyType(0,"EUR");

        // -- Account SENDER --
        Account accountSender = new Account();
        accountSender.setPayment(true);
        accountSender.setDeleted(false);
        User user = new User();
        user.setUserId(id);
        accountSender.setUserId(user);

        // -- Account RECEIVER --
        Account accountReceiver = new Account();
        accountReceiver.setDeleted(false);

        // -- SubAccount SENDER --
        SubAccount subAccountSender = new SubAccount(
                accountSender,
                currencyType,
                150.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountSender));

        // -- SubAccount RECEIVER --
        SubAccount subAccountReceiver = new SubAccount(
                accountReceiver,
                currencyType,
                100.0
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getRecipientIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountReceiver));

        // -- TransactionType --
        TransactionType transactionType = new TransactionType(0,"name",0.0);
        when(transactionTypeRepo.findById(1)).thenReturn(Optional.of(transactionType));


        // -- AccountAccess Sender --
        AccountAccess senderAccess = new AccountAccess(
                accountSender,
                user,
                false, // access is false
                false
        );
        when(accountAccessRepo.findById(
                new AccountAccessPK(subAccountSender.getIban().getIban(), sender.getId())))
                .thenReturn(Optional.of(senderAccess));

        // Then
        assertThatThrownBy(()-> underTest.addTransaction(sender,transactionReq))
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining("Your access to this account is disabled");
    }

    @Test
    void canGetAllTransactionBySubAccount() {

        // Given
        String iban = "iban";
        Integer currency = 0;

        // -- SubAccount --
        SubAccountReq subAccountReq = new SubAccountReq(
                iban,
                currency,
                200.0,
                "EUR"
        );

        // -- User 1 --
        User user = new User(
                "userId",
                "username",
                "lastName",
                "firstname",
                "email",
                "FR",
                Date.valueOf(LocalDate.of(2002,10,31))
        );

        // -- User 2 --
        User user2 = new User(
                "userId",
                "username",
                "lastName",
                "firstname",
                "email",
                "FR",
                Date.valueOf(LocalDate.of(2002,10,31))
        );

        // -- Bank --
        Bank bank = new Bank(
                "swift",
                "name",
                "pwd",
                "address",
                "country",
                new CurrencyType(0,"EUR")
        );

        // -- User1's account --
        Account acc = new Account();
        acc.setIban(iban);
        acc.setPayment(false);
        acc.setSwift(bank);
        acc.setUserId(user);

        // -- User2's account --
        Account acc2 = new Account();
        acc2.setPayment(true);
        acc2.setSwift(bank);
        acc2.setUserId(user2);

        // -- CurrencyType --
        CurrencyType tmpType = new CurrencyType();
        tmpType.setCurrencyId(subAccountReq.getCurrencyType());

        Optional<SubAccount> expectedValue = Optional.of(
                new SubAccount(acc,tmpType,subAccountReq.getCurrentBalance()));
        when(subAccountRepo.findById(any()))
                .thenReturn(expectedValue);

        // -- TransactionType --
        TransactionType transactionType = new TransactionType(1,"type",0.0);


        // -- From user1's account --
        TransactionLog goodTransactionLogSender = new TransactionLog(
                1,
                true,
                transactionType,
                null,
                new SubAccount(acc,tmpType,200.0),
                50.0,
                false,
                "comments"
        );

        // -- To user2's account --
        TransactionLog goodTransactionLogReceiver = new TransactionLog(
                1,
                false,
                transactionType,
                null,
                new SubAccount(acc2,tmpType,100.0),
                50.0,
                false,
                "comments"
        );

        ArrayList<TransactionLog> res = new ArrayList<>();
        res.add(goodTransactionLogReceiver);
        res.add(goodTransactionLogSender);

        when(transactionLogRepo.findAllLinkedToSubAccount(expectedValue.get()))
                .thenReturn(res);

        // When
        List<TransactionReq> result = underTest.getAllTransactionBySubAccount(iban,currency);

        // Then
        assertEquals(1,result.size());
        assertEquals(1,result.get(0).getTransactionId());
        assertEquals(iban,result.get(0).getSenderIban());
        assertEquals(currency,result.get(0).getCurrencyId());
    }

    @Test
    void canGetWithTransactionsBadlyFormatted() {
        // Given
        String iban = "iban";
        Integer currency = 0;

        // -- SubAccount --
        SubAccountReq subAccountReq = new SubAccountReq(
                iban,
                currency,
                200.0,
                "EUR"
        );

        // -- User 1 --
        User user = new User(
                "userId",
                "username",
                "lastName",
                "firstname",
                "email",
                "FR",
                Date.valueOf(LocalDate.of(2002,10,31))
        );

        // -- Bank --
        Bank bank = new Bank(
                "swift",
                "name",
                "pwd",
                "address",
                "country",
                new CurrencyType(0,"EUR")
        );

        // -- User1's account --
        Account acc = new Account();
        acc.setIban(iban);
        acc.setPayment(false);
        acc.setSwift(bank);
        acc.setUserId(user);

        // -- CurrencyType --
        CurrencyType tmpType = new CurrencyType();
        tmpType.setCurrencyId(subAccountReq.getCurrencyType());

        Optional<SubAccount> expectedValue = Optional.of(
                new SubAccount(acc,tmpType,subAccountReq.getCurrentBalance()));
        when(subAccountRepo.findById(any()))
                .thenReturn(expectedValue);

        // -- TransactionType --
        TransactionType transactionType = new TransactionType(1,"type",0.0);


        // -- From user1's account --
        TransactionLog goodTransactionLogSender = new TransactionLog(
                1,
                true,
                transactionType,
                null,
                new SubAccount(acc,tmpType,200.0),
                50.0,
                false,
                "comments"
        );

        ArrayList<TransactionLog> res = new ArrayList<>();
        res.add(goodTransactionLogSender);

        when(transactionLogRepo.findAllLinkedToSubAccount(expectedValue.get()))
                .thenReturn(res);

        when(transactionLogRepo.findBadFormatTransaction()).thenReturn(res);

        // When
        underTest.getAllTransactionBySubAccount(iban,currency);

        // Then
        verify(transactionLogRepo).deleteAllByTransactionId(goodTransactionLogSender.getTransactionId());

        ArgumentCaptor<NotificationReq> notificationCaptor = ArgumentCaptor.forClass(NotificationReq.class);
        ArgumentCaptor<Sender> senderArgumentCaptor = ArgumentCaptor.forClass(Sender.class);
        verify(notificationService).addNotification(senderArgumentCaptor.capture(),notificationCaptor.capture());

        NotificationReq notificationReq = notificationCaptor.getValue();
        Sender sender = senderArgumentCaptor.getValue();

        assertEquals(bank.getSwift(),sender.getId());
        assertEquals(user.getUserId(),notificationReq.getRecipientId());
        assertEquals(5,notificationReq.getNotificationType());
    }

    @Test
    void canExecuteTransaction() {
        // Given
        CurrencyType eur = new CurrencyType(0,"EUR");

        Account acc = new Account();
        acc.setPayment(true);
        acc.setDeleted(false);

        Account acc2 = new Account();
        acc2.setPayment(true);
        acc2.setDeleted(false);


        TransactionLog transactionLogSender = new TransactionLog(
                1,
                true,
                new TransactionType(0,"test",0.0),
                null,
                new SubAccount(acc,eur,200.0),
                50.0,
                false,
                "comments"
        );

        TransactionLog transactionLogReceiver = new TransactionLog(
                1,
                false,
                new TransactionType(0,"test",0.0),
                null,
                new SubAccount(acc2,eur,100.0),
                50.0,
                false,
                "comments"
        );

        // When
        underTest.executeTransaction(transactionLogSender,transactionLogReceiver);

        // Then
        ArgumentCaptor<TransactionLog> transactionCaptor = ArgumentCaptor.forClass(TransactionLog.class);
        verify(transactionLogRepo,times(2)).save(transactionCaptor.capture());
        List<TransactionLog> transactionCaptured = transactionCaptor.getAllValues();

        // -- RECEIVER --
        TransactionLog transactionReceiverCaptured = !transactionCaptured.get(0).getIsSender()
                ? transactionCaptured.get(0) : transactionCaptured.get(1);
        assertTrue(transactionReceiverCaptured.getProcessed());
        assertEquals(100+50, transactionReceiverCaptured.getSubAccount().getCurrentBalance());

        // -- SENDER --
        TransactionLog transactionSenderCaptured = transactionCaptured.get(0).getIsSender()
                ? transactionCaptured.get(0) : transactionCaptured.get(1);
        assertTrue(transactionSenderCaptured.getProcessed());
        assertEquals(200-50,transactionSenderCaptured.getSubAccount().getCurrentBalance());
    }
    
    @Test
    void executeShouldStopWhenPaymentNotEnable(){
        // Given
        CurrencyType currencyType = new CurrencyType(0,"EUR");

        User user = new User(
                "userId",
                "username",
                "lastName",
                "firstname",
                "email",
                "FR",
                Date.valueOf(LocalDate.of(2002,10,31))
        );

        Bank bank = new Bank(
                "swift",
                "name",
                "pwd",
                "address",
                "country",
                new CurrencyType(0,"EUR")
        );

        Account acc = new Account();
        acc.setPayment(false);
        acc.setSwift(bank);
        acc.setUserId(user);

        Account acc2 = new Account();
        acc2.setPayment(true);
        acc2.setSwift(bank);
        acc2.setUserId(user);


        TransactionLog transactionLogSender = new TransactionLog(
                1,
                true,
                null,
                null,
                new SubAccount(acc,currencyType,200.0),
                50.0,
                false,
                "comments"
        );

        TransactionLog transactionLogReceiver = new TransactionLog(
                1,
                false,
                null,
                null,
                new SubAccount(acc2,currencyType,100.0),
                50.0,
                false,
                "comments"
        );


        // When
        underTest.executeTransaction(transactionLogSender,transactionLogReceiver);

        //Then
        verify(transactionLogRepo).deleteAllByTransactionId(transactionLogSender.getTransactionId());
        verify(transactionLogRepo,never()).save(any());
        verify(subAccountRepo,never()).save(any());

        // -- Notification SEND --
        ArgumentCaptor<Sender> senderCaptor = ArgumentCaptor.forClass(Sender.class);
        ArgumentCaptor<NotificationReq> notificationCaptor = ArgumentCaptor.forClass(NotificationReq.class);

        verify(notificationService).addNotification(senderCaptor.capture(),notificationCaptor.capture());

        // -- From --
        Sender bankSender = senderCaptor.getValue();
        assertEquals(
                transactionLogSender.getSubAccount().getIban().getSwift().getSwift(),
                bankSender.getId()
        );

        // -- TO --
        NotificationReq notification = notificationCaptor.getValue();
        assertEquals(
                transactionLogSender.getSubAccount().getIban().getUserId().getUserId(),
                notification.getRecipientId()
        );

    }

    @Test
    void executeShouldStopWhenAccountHasNotEnoughFound(){
        // Given
        CurrencyType currencyType = new CurrencyType(0,"EUR");

        User user = new User(
                "userId",
                "username",
                "lastName",
                "firstname",
                "email",
                "FR",
                Date.valueOf(LocalDate.of(2002,10,31))
        );

        Bank bank = new Bank(
                "swift",
                "name",
                "pwd",
                "address",
                "country",
                new CurrencyType(0,"EUR")
        );

        Account acc = new Account();
        acc.setPayment(true);
        acc.setSwift(bank);
        acc.setUserId(user);

        Account acc2 = new Account();
        acc2.setPayment(true);
        acc2.setSwift(bank);
        acc2.setUserId(user);


        TransactionLog transactionLogSender = new TransactionLog(
                1,
                true,
                new TransactionType(0,"test",0.0),
                null,
                new SubAccount(acc,currencyType,200.0),
                400.0,
                false,
                "comments"
        );

        TransactionLog transactionLogReceiver = new TransactionLog(
                1,
                false,
                new TransactionType(0,"test",0.0),
                null,
                new SubAccount(acc2,currencyType,100.0),
                50.0,
                false,
                "comments"
        );

        // When
        underTest.executeTransaction(transactionLogSender,transactionLogReceiver);

        // Then
        verify(transactionLogRepo).deleteAllByTransactionId(transactionLogSender.getTransactionId());
        verify(transactionLogRepo,never()).save(any());
        verify(subAccountRepo,never()).save(any());

        // -- Notification SEND --
        ArgumentCaptor<Sender> senderCaptor = ArgumentCaptor.forClass(Sender.class);
        ArgumentCaptor<NotificationReq> notificationCaptor = ArgumentCaptor.forClass(NotificationReq.class);

        verify(notificationService).addNotification(senderCaptor.capture(),notificationCaptor.capture());

        // -- From --
        Sender bankSender = senderCaptor.getValue();
        assertEquals(
                transactionLogSender.getSubAccount().getIban().getSwift().getSwift(),
                bankSender.getId()
        );

        // -- TO --
        NotificationReq notification = notificationCaptor.getValue();
        assertEquals(
                transactionLogSender.getSubAccount().getIban().getUserId().getUserId(),
                notification.getRecipientId()
        );
    }

    @Test
    void executeShouldStropWhenAccountSenderDeleted(){
        // Given
        CurrencyType currencyType = new CurrencyType(0,"EUR");

        User user = new User(
                "userId",
                "username",
                "lastName",
                "firstname",
                "email",
                "FR",
                Date.valueOf(LocalDate.of(2002,10,31))
        );

        Bank bank = new Bank(
                "swift",
                "name",
                "pwd",
                "address",
                "country",
                new CurrencyType(0,"EUR")
        );

        Account accSender = new Account();
        accSender.setPayment(true);
        accSender.setSwift(bank);
        accSender.setUserId(user);
        accSender.setDeleted(true);

        Account accReceiver = new Account();
        accReceiver.setPayment(true);
        accReceiver.setSwift(bank);
        accReceiver.setUserId(user);
        accReceiver.setDeleted(false);


        TransactionLog transactionLogSender = new TransactionLog(
                1,
                true,
                new TransactionType(0,"test",0.0),
                null,
                new SubAccount(accSender,currencyType,200.0),
                40.0,
                false,
                "comments"
        );

        TransactionLog transactionLogReceiver = new TransactionLog(
                1,
                false,
                new TransactionType(0,"test",0.0),
                null,
                new SubAccount(accReceiver,currencyType,100.0),
                50.0,
                false,
                "comments"
        );

        // When
        underTest.executeTransaction(transactionLogSender,transactionLogReceiver);

        // Then
        verify(transactionLogRepo).deleteAllByTransactionId(transactionLogSender.getTransactionId());
        verify(transactionLogRepo,never()).save(any());
        verify(subAccountRepo,never()).save(any());

        // -- Notification SEND --
        ArgumentCaptor<Sender> senderCaptor = ArgumentCaptor.forClass(Sender.class);
        ArgumentCaptor<NotificationReq> notificationCaptor = ArgumentCaptor.forClass(NotificationReq.class);

        verify(notificationService).addNotification(senderCaptor.capture(),notificationCaptor.capture());

        // -- From --
        Sender bankSender = senderCaptor.getValue();
        assertEquals(
                transactionLogSender.getSubAccount().getIban().getSwift().getSwift(),
                bankSender.getId()
        );

        // -- TO --
        NotificationReq notification = notificationCaptor.getValue();
        assertEquals(
                transactionLogSender.getSubAccount().getIban().getUserId().getUserId(),
                notification.getRecipientId()
        );
    }

    @Test
    void executeShouldStropWhenAccountReceiverDeleted(){
        // Given
        CurrencyType currencyType = new CurrencyType(0,"EUR");

        User user = new User(
                "userId",
                "username",
                "lastName",
                "firstname",
                "email",
                "FR",
                Date.valueOf(LocalDate.of(2002,10,31))
        );

        Bank bank = new Bank(
                "swift",
                "name",
                "pwd",
                "address",
                "country",
                new CurrencyType(0,"EUR")
        );

        Account accSender = new Account();
        accSender.setPayment(true);
        accSender.setSwift(bank);
        accSender.setUserId(user);
        accSender.setDeleted(false);

        Account accReceiver = new Account();
        accReceiver.setPayment(true);
        accReceiver.setSwift(bank);
        accReceiver.setUserId(user);
        accReceiver.setDeleted(true);


        TransactionLog transactionLogSender = new TransactionLog(
                1,
                true,
                new TransactionType(0,"test",0.0),
                null,
                new SubAccount(accSender,currencyType,200.0),
                40.0,
                false,
                "comments"
        );

        TransactionLog transactionLogReceiver = new TransactionLog(
                1,
                false,
                new TransactionType(0,"test",0.0),
                null,
                new SubAccount(accReceiver,currencyType,100.0),
                50.0,
                false,
                "comments"
        );

        // When
        underTest.executeTransaction(transactionLogSender,transactionLogReceiver);

        // Then
        verify(transactionLogRepo).deleteAllByTransactionId(transactionLogSender.getTransactionId());
        verify(transactionLogRepo,never()).save(any());
        verify(subAccountRepo,never()).save(any());

        // -- Notification SEND --
        ArgumentCaptor<Sender> senderCaptor = ArgumentCaptor.forClass(Sender.class);
        ArgumentCaptor<NotificationReq> notificationCaptor = ArgumentCaptor.forClass(NotificationReq.class);

        verify(notificationService).addNotification(senderCaptor.capture(),notificationCaptor.capture());

        // -- From --
        Sender bankSender = senderCaptor.getValue();
        assertEquals(
                transactionLogSender.getSubAccount().getIban().getSwift().getSwift(),
                bankSender.getId()
        );

        // -- TO --
        NotificationReq notification = notificationCaptor.getValue();
        assertEquals(
                transactionLogSender.getSubAccount().getIban().getUserId().getUserId(),
                notification.getRecipientId()
        );
    }
}