package com.example.demo.repository;

import com.example.demo.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepoTest {

    @Autowired
    private UserRepo underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByUsername() {
        //Given
        User user = new User(
                "testId",
                "username",
                "lastname",
                "firstname",
                "email",
                "password",
                "language",
                Date.valueOf(LocalDate.of(2002,10,31))
        );
        underTest.save(user);

        User res = underTest.findUserByUsername(user.getUsername()).get();

        //Then
        assertEquals(user.getUsername(),res.getUsername());
    }

    @Test
    void existsByUsername() {
        // Given
        User user = new User(
                "testId",
                "imATest",
                "lastName",
                "firstName",
                "test@email.com",
                "passwordTested",
                "EN",
                Date.valueOf(LocalDate.of(2002,10,31))
        );
        underTest.save(user);

        //When
        boolean shouldBeTrue = underTest.existsByUsername("imATest");
        boolean shouldBeFalse = underTest.existsByUsername("notAnUsername");

        //Then
        assertTrue(shouldBeTrue);
        assertFalse(shouldBeFalse);
    }

    @Test
    void existsByEmail() {
        // Given
        User user = new User(
                "testId",
                "imATest",
                "lastName",
                "firstName",
                "test@email.com",
                "passwordTested",
                "EN",
                Date.valueOf(LocalDate.of(2002,10,31))
        );
        underTest.save(user);

        // When
        boolean shouldBeTrue = underTest.existsByEmail("test@email.com");
        boolean shouldBeFalse = underTest.existsByEmail("notAnEmail");

        // Then
        assertTrue(shouldBeTrue);
        assertFalse(shouldBeFalse);


    }
}