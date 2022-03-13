package com.example.demo.repository;

import com.example.demo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
public class UserRepoTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepo userRepo;

    @Test
    public void testGetUser() {

        userRepo.save(new User("1234","username","lastName",
                "firstName","email","password","EN"));

        assertTrue(userRepo.existsById("1234"));
    }

}
