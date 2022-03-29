package com.example.demo.repository;

import com.example.demo.model.Bank;
import com.example.demo.model.Notification;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface NotificationRepo extends JpaRepository<Notification, Integer> {
    /*
    @Query("SELECT notif " +
            "FROM Notification notif " +
            "where notif.userId = ?1")*/
    ArrayList<Notification> findAllByUserId(User user);

    @Query("SELECT notif " +
            "FROM Notification notif " +
            "where notif.bankId = ?1")
    ArrayList<Notification> findAllByBankId(Bank bank);
}
