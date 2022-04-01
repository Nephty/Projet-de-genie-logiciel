package com.example.demo.repository;

import com.example.demo.model.Bank;
import com.example.demo.model.CurrencyType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BankRepoTest {

    @Autowired
    private BankRepo underTest;

    @Autowired
    private CurrencyTypeRepo currencyTypeRepo;

    @BeforeEach
    void setUp() {
        CurrencyType currencyType = new CurrencyType(0,"EUR");
        currencyTypeRepo.save(currencyType);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void existsByName() {
        // Given
        Bank bank = new Bank(
               "swift",
               "name",
                "pwd",
                "address",
                "country",
                currencyTypeRepo.getById(0)
        );
        underTest.save(bank);

        //When
        boolean shouldBeTrue = underTest.existsByName("name");
        boolean shouldBeFalse = underTest.existsByName("notAName");

        //Then
        assertTrue(shouldBeTrue);
        assertFalse(shouldBeFalse);
    }
}