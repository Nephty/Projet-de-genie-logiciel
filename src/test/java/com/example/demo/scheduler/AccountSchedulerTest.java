package com.example.demo.scheduler;

import com.example.demo.model.Account;
import com.example.demo.model.AccountType;
import com.example.demo.model.SubAccount;
import com.example.demo.repository.AccountRepo;
import com.example.demo.repository.SubAccountRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountSchedulerTest {
    @Mock
    AccountRepo accountRepo;
    @Mock
    SubAccountRepo subAccountRepo;
    @Captor
    private ArgumentCaptor<ArrayList<SubAccount>> captor;

    private AccountScheduler underTest;

    @BeforeEach
    void setup() {
        underTest = new AccountScheduler(accountRepo, subAccountRepo);
    }
    @Test
    void canProcessAccount_interest() {
        AccountType accountType = new AccountType(
                1,
                "testType",
                0.1,
                null,
                18,
                null
        );
        Date now = Date.valueOf(LocalDate.now());


        Account acc = new Account();
        acc.setIban("test");
        acc.setAccountTypeId(accountType);
        acc.setNextProcess(now);

        ArrayList<Account> tmp = new ArrayList<>();
        tmp.add(acc);
        when(accountRepo.findAccountsToProcess()).thenReturn(tmp);

        SubAccount subAccount = new SubAccount();
        subAccount.setIban(acc);
        subAccount.setCurrentBalance(100.0);

        ArrayList<SubAccount> tmp2 = new ArrayList<>();
        tmp2.add(subAccount);
        when(subAccountRepo.findAllByIban(acc)).thenReturn(tmp2);

        // When
        underTest.processAccount();

        // Then
        verify(subAccountRepo).saveAll(captor.capture());
        ArrayList<SubAccount> res = captor.getValue();
        assertEquals(1,res.size());

        assertEquals(100*(1+0.1),res.get(0).getCurrentBalance());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo).save(accountCaptor.capture());
        Account res2 = accountCaptor.getValue();
        assertEquals(Date.valueOf(now.toLocalDate().plusYears(1)),res2.getNextProcess());
    }

    @Test
    void canProcessAccount_fee() {
        AccountType accountType = new AccountType(
                1,
                "testType",
                null,
                0.1,
                18,
                null
        );
        Date now = Date.valueOf(LocalDate.now());

        Account acc = new Account();
        acc.setIban("test");
        acc.setAccountTypeId(accountType);
        acc.setNextProcess(now);

        ArrayList<Account> tmp = new ArrayList<>();
        tmp.add(acc);
        when(accountRepo.findAccountsToProcess()).thenReturn(tmp);

        SubAccount subAccount = new SubAccount();
        subAccount.setIban(acc);
        subAccount.setCurrentBalance(100.0);

        ArrayList<SubAccount> tmp2 = new ArrayList<>();
        tmp2.add(subAccount);
        when(subAccountRepo.findAllByIban(acc)).thenReturn(tmp2);
        // When
        underTest.processAccount();

        // Then
        verify(subAccountRepo).saveAll(captor.capture());
        ArrayList<SubAccount> res = captor.getValue();
        assertEquals(1,res.size());

        assertEquals(100-100*0.1,res.get(0).getCurrentBalance());
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo).save(accountCaptor.capture());
        Account res2 = accountCaptor.getValue();
        assertEquals(Date.valueOf(now.toLocalDate().plusYears(1)),res2.getNextProcess());
    }
}
