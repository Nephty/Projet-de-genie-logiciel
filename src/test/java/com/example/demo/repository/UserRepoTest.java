package com.example.demo.repository;

import com.example.demo.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

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
                "language"
        );
        underTest.save(user);

        User res = underTest.findUserByUsername(user.getUsername()).get();

        //Then
        assertEquals(user.getUsername(),res.getUsername());


    }

    @Test
    @Disabled
    void existsByUsername() {
        // TODO: 3/27/22  
    }

    @Test
    @Disabled
    void existsByEmail() {
        // TODO: 3/27/22  
    }
}