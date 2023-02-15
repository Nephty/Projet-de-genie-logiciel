package com.example.demo.repository;

import com.example.demo.model.Bank;
import com.example.demo.model.Notification;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;


/**
 * All the db request on the {@link Notification} table.
 */
public interface NotificationRepo extends JpaRepository<Notification, Integer> {

    /**
     * Find all Notifications related to a User
     * @param user The user whose notifications we want to find
     * @return All the notifications of the User
     */
    ArrayList<Notification> findAllByUserId(User user);

    /**
     * Find all Notifications related to a Bank
     * @param bank The Bank from which we want to find the notifications.
     * @return All the notifications of the bank
     */
    ArrayList<Notification> findAllByBankId(Bank bank);
}
