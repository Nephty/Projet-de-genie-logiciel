package com.example.demo.scheduler;

import com.example.demo.model.*;
import com.example.demo.repository.TransactionLogRepo;
import com.example.demo.service.TransactionLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionSchedulerTest {

    @Mock
    private TransactionLogRepo transactionLogRepo;
    @Mock
    private TransactionLogService transactionLogService;
    @Mock
    private Clock clock;

    private TransactionScheduler underTest;

    @BeforeEach
    void setUp() {
        underTest = new TransactionScheduler(transactionLogRepo,transactionLogService,clock);
    }

    @Test
    void performWithTransactionBadlyFormatted(){
        // Given
        // Set time to monday -> Can perform transaction
        LocalDateTime dateTime = LocalDateTime.of(2022,4,25,12,10);
        Instant instant = ZonedDateTime.of(dateTime,ZoneId.systemDefault()).toInstant();
        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

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

        ArrayList<TransactionLog> transactionToPerform = new ArrayList<>();
        transactionToPerform.add(transactionLogSender);

        when(transactionLogRepo.findAllToExecute()).thenReturn(transactionToPerform);

        when(transactionLogRepo.findBadFormatTransaction()).thenReturn(transactionToPerform);

        // When
        underTest.performDueTransactions();

        // Then
        verify(transactionLogRepo).deleteAllByTransactionId(transactionLogSender.getTransactionId());

        verify(transactionLogService).sendBadFormatTransaction(transactionLogSender);

    }

    @Test
    void canPerformDueTransactions() {
        // Given
        // Set time to monday -> Can perform transaction
        LocalDateTime dateTime = LocalDateTime.of(2022,4,25,12,10);
        Instant instant = ZonedDateTime.of(dateTime,ZoneId.systemDefault()).toInstant();
        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        // -- CURRENCY --
        CurrencyType currencyType = new CurrencyType(0,"EUR");

        // -- USER --
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

        // -- Account SENDER --
        Account accSender = new Account();
        accSender.setPayment(true);
        accSender.setSwift(bank);
        accSender.setUserId(user);
        accSender.setDeleted(false);

        // -- Account RECEIVER --
        Account accReceiver = new Account();
        accReceiver.setPayment(true);
        accReceiver.setSwift(bank);
        accReceiver.setUserId(user);
        accReceiver.setDeleted(false);

        // -- TransactionType --
        TransactionType transactionType = new TransactionType(1,"type",0.0);


        TransactionLog transactionLogSender = new TransactionLog(
                1,
                true,
                transactionType,
                null,
                new SubAccount(accSender,currencyType,200.0),
                40.0,
                false,
                "comments"
        );

        TransactionLog transactionLogReceiver = new TransactionLog(
                1,
                false,
                transactionType,
                null,
                new SubAccount(accReceiver,currencyType,100.0),
                40.0,
                false,
                "comments"
        );

        ArrayList<TransactionLog> transactionToPerform = new ArrayList<>();
        transactionToPerform.add(transactionLogSender);
        transactionToPerform.add(transactionLogReceiver);

        when(transactionLogRepo.findAllToExecute()).thenReturn(transactionToPerform);

        // When
        underTest.performDueTransactions();

        // Then

        ArgumentCaptor<TransactionLog> transactionCaptorSender = ArgumentCaptor.forClass(TransactionLog.class);
        ArgumentCaptor<TransactionLog> transactionCaptorReceiver = ArgumentCaptor.forClass(TransactionLog.class);
        verify(transactionLogService).executeTransaction(
                transactionCaptorSender.capture(),transactionCaptorReceiver.capture()
        );
        // We just have to test if we call executeTransaction() with the good parameters because the method is already tested.
        TransactionLog transactionLogSent = transactionCaptorSender.getValue();
        TransactionLog transactionLogReceived = transactionCaptorReceiver.getValue();

        assertEquals(1,transactionLogSent.getTransactionId());
        assertEquals(1,transactionLogReceived.getTransactionId());

        assertTrue(transactionLogSent.getIsSender());
        assertFalse(transactionLogReceived.getIsSender());
    }

    @Test
    void performDueTransactionsShouldStopOnWeekend() {
        // Given
        // Set time to Sunday -> Can't perform transaction
        LocalDateTime dateTime = LocalDateTime.of(2022,4,23,12,10);
        Instant instant = ZonedDateTime.of(dateTime,ZoneId.systemDefault()).toInstant();
        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        // when
        underTest.performDueTransactions();

        // Then
        verify(transactionLogService,never()).executeTransaction(any(),any());
    }
}