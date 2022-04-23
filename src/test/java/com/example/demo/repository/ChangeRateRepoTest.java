package com.example.demo.repository;

import com.example.demo.model.ChangeRate;
import com.example.demo.model.CurrencyType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ChangeRateRepoTest {
    @Autowired
    private ChangeRateRepo underTest;
    @Autowired
    private CurrencyTypeRepo currencyTypeRepo;

    @BeforeEach
    void setUp() {
        CurrencyType eur = new CurrencyType(0,"EUR");
        currencyTypeRepo.save(eur);
        CurrencyType usd = new CurrencyType(1,"USD");
        currencyTypeRepo.save(usd);
        CurrencyType gbp = new CurrencyType(2,"GBP");
        currencyTypeRepo.save(gbp);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findAllByCurrency() {
        // Given
        ChangeRate euroToUsd1 = new ChangeRate(
                currencyTypeRepo.getById(0),
                currencyTypeRepo.getById(1),
                0.98,
                Date.valueOf(LocalDate.of(2022,4,20))
        );
        underTest.save(euroToUsd1);

        ChangeRate euroToGbp = new ChangeRate(
                currencyTypeRepo.getById(0),
                currencyTypeRepo.getById(2),
                1.02,
                Date.valueOf(LocalDate.of(2022,4,20))
        );
        underTest.save(euroToGbp);

        ChangeRate euroToUsd2 = new ChangeRate(
                currencyTypeRepo.getById(0),
                currencyTypeRepo.getById(1),
                0.96,
                Date.valueOf(LocalDate.of(2022,4,21))
        );
        underTest.save(euroToUsd2);

        // When
        List<ChangeRate> res = underTest.findAllByCurrency(currencyTypeRepo.getById(0),currencyTypeRepo.getById(1));

        // Then
        assertEquals(2,res.size());
        assertEquals(0,res.get(0).getCurrencyTypeFrom().getCurrencyId());
        assertEquals(1,res.get(0).getCurrencyTypeTo().getCurrencyId());
    }

    @Test
    void findLastFetch() {
        // Given
        ChangeRate euroToUsd1 = new ChangeRate(
                currencyTypeRepo.getById(0),
                currencyTypeRepo.getById(1),
                0.98,
                Date.valueOf(LocalDate.of(2022,4,22))
        );
        underTest.save(euroToUsd1);

        ChangeRate euroToGbp = new ChangeRate(
                currencyTypeRepo.getById(0),
                currencyTypeRepo.getById(2),
                1.02,
                Date.valueOf(LocalDate.of(2022,4,20))
        );
        underTest.save(euroToGbp);

        ChangeRate euroToUsd2 = new ChangeRate(
                currencyTypeRepo.getById(0),
                currencyTypeRepo.getById(1),
                0.96,
                Date.valueOf(LocalDate.of(2022,4,21))
        );
        underTest.save(euroToUsd2);

        // When
        Date res = underTest.findLastFetch();

        // Then
        assertEquals(Date.valueOf(LocalDate.of(2022,4,22)),res);
    }
}