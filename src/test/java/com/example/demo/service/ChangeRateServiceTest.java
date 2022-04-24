package com.example.demo.service;

import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.ChangeRate;
import com.example.demo.model.CompositePK.ChangeRatePK;
import com.example.demo.model.CurrencyType;
import com.example.demo.repository.ChangeRateRepo;
import com.example.demo.repository.CurrencyTypeRepo;
import com.example.demo.request.ChangeRateReq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeRateServiceTest {

    @Mock
    private ChangeRateRepo changeRateRepo;
    @Mock
    private CurrencyTypeRepo currencyTypeRepo;

    private ChangeRateService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ChangeRateService(changeRateRepo,currencyTypeRepo);
    }

    @Test
    void canGetChangeRate() {
        // Given
        Date date = Date.valueOf(LocalDate.of(2022,4,20));
        int currencyFrom = 0; // EUR
        int currencyTo = 1; // USD

        CurrencyType eur = new CurrencyType(0,"EUR");
        CurrencyType usd = new CurrencyType(1,"USD");


        ChangeRate euroToUsd1 = new ChangeRate(
                eur,
                usd,
                0.98,
                Date.valueOf(LocalDate.of(2022,4,20))
        );

        when(changeRateRepo.findById(new ChangeRatePK(date,currencyFrom,currencyTo)))
                .thenReturn(Optional.of(euroToUsd1));

        // When
        ChangeRateReq res = underTest.getChangeRate(date,currencyFrom,currencyTo);

        // Then
        assertEquals(currencyFrom,res.getCurrencyTypeFrom());
        assertEquals(currencyTo,res.getCurrencyTypeTo());
        assertEquals(date,res.getDate());
    }

    @Test
    void getChangeRateShouldThrowWhenChangeRateNotFound(){
        // Given
        Date date = Date.valueOf(LocalDate.of(2022,4,20));
        int currencyFrom = 0; // EUR
        int currencyTo = 1; // USD

        // Then
        assertThatThrownBy(() -> underTest.getChangeRate(date,currencyFrom,currencyTo))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("No change rate on this date "+date +" between those currencies : "+currencyFrom+" : "+currencyTo);
    }

    @Test
    void getChangeRateHistory() {
        // Given
        int currencyFrom = 0; // EUR
        int currencyTo = 1; // USD
        Date date = Date.valueOf(LocalDate.of(2022,4,20));

        CurrencyType eur = new CurrencyType(0,"EUR");
        when(currencyTypeRepo.findById(0)).thenReturn(Optional.of(eur));

        CurrencyType usd = new CurrencyType(1,"USD");
        when(currencyTypeRepo.findById(1)).thenReturn(Optional.of(usd));

        ChangeRate euroToUsd1 = new ChangeRate(
                eur,
                usd,
                0.98,
                date
        );

        ArrayList<ChangeRate> res = new ArrayList<>();
        res.add(euroToUsd1);
        when(changeRateRepo.findAllByCurrency(eur,usd)).thenReturn(res);

        // When
        List<ChangeRateReq> result = underTest.getChangeRateHistory(currencyFrom,currencyTo);

        // Then
        assertEquals(1,result.size());
        // We are also testing if the constructor of changeRateReq is working
        assertEquals(date,result.get(0).getDate());

        assertEquals(0,result.get(0).getCurrencyTypeFrom());
        assertEquals("EUR", result.get(0).getCurrencyTypeFromName());

        assertEquals(1,result.get(0).getCurrencyTypeTo());
        assertEquals("USD",result.get(0).getCurrencyTypeToName());

        assertEquals(0.98,result.get(0).getChangeRate());

    }

    @Test
    void getChangeRateHistoryShouldThrowWhenCurrencyFromNotFound() {
        // Given
        int currencyFrom = 0; // EUR
        int currencyTo = 1; // USD

        // Then
        assertThatThrownBy(() -> underTest.getChangeRateHistory(currencyFrom,currencyTo))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("No currency with this id "+currencyFrom);
    }

    @Test
    void getChangeRateHistoryShouldThrowWhenCurrencyToNotFound() {
        // Given
        int currencyFrom = 0; // EUR
        int currencyTo = 1; // USD

        CurrencyType eur = new CurrencyType(0,"EUR");
        when(currencyTypeRepo.findById(0)).thenReturn(Optional.of(eur));

        // Then
        assertThatThrownBy(() -> underTest.getChangeRateHistory(currencyFrom,currencyTo))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("No currency with this id "+currencyTo);


    }

}