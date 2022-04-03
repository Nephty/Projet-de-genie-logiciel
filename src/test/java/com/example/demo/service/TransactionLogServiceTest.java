package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.AccountAccessRepo;
import com.example.demo.repository.SubAccountRepo;
import com.example.demo.repository.TransactionLogRepo;
import com.example.demo.repository.TransactionTypeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    private TransactionLogService underTest;

    @BeforeEach
    void setUp() {
        underTest = new TransactionLogService(
                transactionLogRepo,
                subAccountRepo,
                transactionTypeRepo,
                accountAccessRepo
        );
    }

    @Test
    @Disabled
    void addTransaction() {
        // TODO: 4/3/22 test 
    }

    @Test
    @Disabled
    void getAllTransactionBySubAccount() {
        // TODO: 4/3/22 test
    }

    @Test
    void canExecuteTransaction() {
        // Given
        CurrencyType currencyType = new CurrencyType(0,"EUR");

        Account acc = new Account();
        acc.setPayment(true);

        Account acc2 = new Account();
        acc.setPayment(true);


        TransactionLog transactionLogSender = new TransactionLog(
                1,
                0,
                null,
                null,
                new SubAccount(acc,currencyType,200.0),
                50.0,
                false,
                "comments"
        );

        TransactionLog transactionLogReceiver = new TransactionLog(
                1,
                1,
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
        TransactionLog transactionReceiverCaptured = transactionCaptured.get(0).getDirection()==0
                ? transactionCaptured.get(0) : transactionCaptured.get(1);
        assertTrue(transactionReceiverCaptured.getProcessed());
        assertEquals(100+50, transactionReceiverCaptured.getSubAccount().getCurrentBalance());

        // -- SENDER --
        TransactionLog transactionSenderCaptured = transactionCaptured.get(0).getDirection()==1
                ? transactionCaptured.get(0) : transactionCaptured.get(1);
        assertTrue(transactionSenderCaptured.getProcessed());
        assertEquals(200-50,transactionSenderCaptured.getSubAccount().getCurrentBalance());
    }
    
    @Test
    @Disabled
    void executeShouldThrowWhenPaymentNotEnable(){
        // TODO: 4/3/22 test that the method throws an exception 
    }
    
    @Test
    @Disabled
    void executeShouldThrowWhenAmountIsLowerOrEqualToZero(){
        // TODO: 4/3/22 test that the method throws an exception 
    }
    
    @Test
    @Disabled
    void executeShouldThrowWhenAccountHasNotEnoughFound(){
        // TODO: 4/3/22 test that the method throws an exception  
    }
}