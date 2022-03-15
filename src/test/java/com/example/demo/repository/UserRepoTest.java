package com.example.demo.repository;

import com.example.demo.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
public class UserRepoTest {

    @Autowired
    private UserRepo underTest;

    @Test
    void itShouldCheckIfUserIsReturnedWithoutPassword() {
        //given
        String id = "testId";
        User user = new User(
                id,
                "imATest",
                "lastName",
                "firstName",
                "test@email.com",
                "passwordTested",
                "EN"
        );
        underTest.save(user);

        //when
        User result = underTest.findByIdWithoutPassword(id).get();

        //then
        assertThat(result.getUserID()).isEqualTo(id);
        assertThat(result.getPassword()).isNull();
    }

    @Test
    void findByUsername() {
        //given
        String username = "test";
        User user = new User(
                "testId",
                username,
                "lastName",
                "firstName",
                "test@gmail.com",
                "passwordTested",
                "EN"
        );
        underTest.save(user);

        //when
        Optional<User> result = underTest.findByUsername(username);

        //then
        if (result.isEmpty()){
            fail();
        }
        User user_res = result.get();
        assertThat(user_res.getUsername()).isEqualTo(username);
        //TODO : Find a way to set the password to null every time we fetch a User.
        //assertThat(result.getPassword()).isNull();

    }
}
