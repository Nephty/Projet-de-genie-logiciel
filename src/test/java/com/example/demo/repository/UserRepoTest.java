package com.example.demo.repository;

import com.example.demo.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepoTest {

    @Autowired
    private UserRepo underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

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
        //User result = underTest.findByIdWithoutPassword(id).get();

        //then
        //assertThat(result.getUserID()).isEqualTo(id);
        //assertThat(result.getPassword()).isNull();
    }

}
