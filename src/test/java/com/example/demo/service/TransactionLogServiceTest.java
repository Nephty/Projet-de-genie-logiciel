package com.example.demo.service;

import com.example.demo.exception.throwables.AuthorizationException;
import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.*;
import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.other.Sender;
import com.example.demo.repository.*;
import com.example.demo.request.NotificationReq;
import com.example.demo.request.SubAccountReq;
import com.example.demo.request.TransactionReq;
import com.example.demo.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    @Disabled
    void canAddTransaction() {
        // TODO: 4/3/22 test can add Transaction

        // Given

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

        // -- SubAccount SENDER
        SubAccount subAccountSender = new SubAccount(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
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

        // -- SubAccount SENDER --
        SubAccount subAccountSender = new SubAccount(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
        );
        when(subAccountRepo.findById(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
        )).thenReturn(Optional.of(subAccountSender));

        // -- SubAccount RECEIVER --
        SubAccount subAccountReceiver = new SubAccount(
                new SubAccountPK(transactionReq.getRecipientIban(), transactionReq.getCurrencyId())
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
    @Disabled
    void canInstantiateTransaction(){
        // TODO: 4/5/22 test
    }

    @Test
    @Disabled
    void instantiateTransactionShouldThrowWhenAccountCantMakePayment(){
        // TODO: 4/5/22 test
    }

    @Test
    @Disabled
    void instantiateTransactionShouldThrowWhenAmountLowerOrEqualToZero(){
        // TODO: 4/5/22 lower or equal to zero
    }

    @Test
    @Disabled
    void instantiateTransactionShouldThrowWhenUserDontHaveAccessToAccount(){
        // TODO: 4/5/22 no access
    }

    @Test
    @Disabled
    void instantiateTransactionShouldThrowWhenAccessDisabled(){
        // TODO: 4/5/22 access disabled
    }

    @Test
    void canGetAllTransactionBySubAccount() {
        // TODO: 4/3/22 test

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
                "FR"
        );

        // -- User 2 --
        User user2 = new User(
                "userId",
                "username",
                "lastName",
                "firstname",
                "email",
                "FR"
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
    void canExecuteTransaction() {
        // Given
        CurrencyType currencyType = new CurrencyType(0,"EUR");

        Account acc = new Account();
        acc.setPayment(true);

        Account acc2 = new Account();
        acc2.setPayment(true);


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
                "FR"
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
                "FR"
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
                null,
                null,
                new SubAccount(acc,currencyType,200.0),
                400.0,
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