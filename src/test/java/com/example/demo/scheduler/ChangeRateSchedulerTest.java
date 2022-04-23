package com.example.demo.scheduler;

import com.example.demo.repository.ChangeRateRepo;
import com.example.demo.repository.CurrencyTypeRepo;
import com.mashape.unirest.http.Unirest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeRateSchedulerTest {

    @Mock
    private CurrencyTypeRepo currencyTypeRepo;
    @Mock
    private ChangeRateRepo changeRateRepo;

    private ChangeRateScheduler underTest;

    @BeforeEach
    void setUp() {
        underTest = new ChangeRateScheduler(changeRateRepo,currencyTypeRepo);
    }

    @Test
    @Disabled
    void fetchAllChangeRates() {
        // TODO: 4/22/22
    }

    @Test
    void fetchAllChangeRatesShouldStopWhenAlreadyDone() {
        // Given 
        when(changeRateRepo.findLastFetch()).thenReturn(Date.valueOf(LocalDate.MAX));
        
        // When
        underTest.fetchAllChangeRates();
        
        // Then
        verify(changeRateRepo, never()).save(any());
    }
}