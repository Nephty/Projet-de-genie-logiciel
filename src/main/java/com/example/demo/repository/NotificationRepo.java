package com.example.demo.repository;

import com.example.demo.model.Bank;
import com.example.demo.model.Notification;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface NotificationRepo extends JpaRepository<Notification, Integer> {


    /*@Query("select n " +
            "from Notification n " +
            "where n.userId = ?1")*/
    ArrayList<Notification> findAllByUserId(User user);


    @Query("select n " +
            "from Notification n " +
            "where n.bankId = ?1")
    ArrayList<Notification> findAllByBankId(Bank bank);
}
