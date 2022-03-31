package com.example.demo.repository;

import com.example.demo.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NotificationRepoTest {

    @Autowired
    private NotificationRepo underTest;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BankRepo bankRepo;
    @Autowired
    private CurrencyTypeRepo currencyRepo;

    @Autowired
    private NotificationTypeRepo notificationTypeRepo;

    @BeforeEach
    void setUp() {
        User user = new User(
                "testId",
                "username",
                "lastname",
                "firstname",
                "email",
                "EN",
                "pwd"
        );
        userRepo.save(user);

        User user2 = new User(
                "test2Id",
                "username2",
                "lastname2",
                "firstname2",
                "email2",
                "EN",
                "pwd"
        );
        userRepo.save(user2);

        CurrencyType currencyType = new CurrencyType(0,"EUR");
        currencyRepo.save(currencyType);

        Bank bank = new Bank(
                "testSwift",
                "test",
                "password",
                "address",
                "English",
                currencyType
        );
        bankRepo.save(bank);

        NotificationType notificationType = new NotificationType(
                1,
                "testType"
        );
        notificationTypeRepo.save(notificationType);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    @Disabled
    void findAllByUserId() {
        //Given

        // - NOTIFICATION 1 - (user1)
        Notification notification = new Notification(
                0,
                notificationTypeRepo.getById(1),
                "comment",
                null,
                "status",
                userRepo.getById("testId"),
                bankRepo.getById("testSwift"),
                true
        );
        underTest.save(notification);

        // - Notification 2 - (user1)
        Notification notification2 = new Notification();
        notification2.setNotificationType(notificationTypeRepo.getById(1));
        notification2.setUserId(userRepo.getById("testId"));
        notification2.setBankId(bankRepo.getById("testSwift"));
        //underTest.save(notification2);

        // - Notification 3 (user2) -
        Notification notification3 = new Notification();
        notification3.setNotificationType(notificationTypeRepo.getById(1));
        notification3.setUserId(userRepo.getById("test2Id"));
        notification3.setBankId(bankRepo.getById("testSwift"));
        //underTest.save(notification3);

        //When
        ArrayList<Notification> result = underTest.findAllByUserId(userRepo.getById("testId"));

        //Then
        //assertEquals(2,result.size());
        assertTrue(result.contains(notification));
        assertTrue(result.contains(notification2));



    }

    @Test
    @Disabled
    void findAllByBankId() {
        //TODO
    }
}