package com.example.demo.scheduler;

import com.example.demo.model.SubAccount;
import com.example.demo.repository.AccountRepo;
import com.example.demo.repository.SubAccountRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountSchedulerTest {
    @Mock
    AccountRepo accountRepo;
    @Mock
    SubAccountRepo subAccountRepo;

    private AccountScheduler underTest;

    @BeforeEach
    void setup() {
        underTest = new AccountScheduler(accountRepo, subAccountRepo);
    }
    @Test
    void canProcessAccount() {}
}
